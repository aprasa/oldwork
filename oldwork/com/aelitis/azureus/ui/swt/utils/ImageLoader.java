/*
 * Created on Jun 7, 2006 2:31:26 PM
 * Copyright (C) 2006 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * AELITIS, SAS au capital de 46,603.30 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 */
package com.aelitis.azureus.ui.swt.utils;

import java.io.InputStream;
import java.util.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;

import org.gudy.azureus2.ui.swt.Utils;

import com.aelitis.azureus.ui.skin.SkinProperties;

/**
 * Loads images from skin.  
 * <p>
 * Will look for special suffixes (over, down, disabled) and try to
 * load resources using base key and suffix. ie. loadImage("foo-over") when
 * foo=image.png, will load image-over.png
 * <p>
 * Will also create own disabled images if base image found and no disabled
 * image found.  Disabled opacity can be set via imageloader.disabled-opacity 
 * key
 * 
 * @author TuxPaper
 * @created Jun 7, 2006
 *
 */
public class ImageLoader
{

	private static final boolean DEBUG_UNLOAD = false;

	private final String[] sSuffixChecks = {
		"-over",
		"-down",
		"-disabled",
		"-selected",
	};

	private Display display;

	public static Image noImage;

	private final Map mapImages;

	private final ArrayList notFound;

	private SkinProperties skinProperties;

	private final ClassLoader classLoader;

	private int disabledOpacity;

	public ImageLoader(ClassLoader classLoader, Display display,
			SkinProperties skinProperties) {
		this.classLoader = classLoader;
		mapImages = new HashMap();
		notFound = new ArrayList();
		this.display = display;
		this.skinProperties = skinProperties;
		disabledOpacity = skinProperties.getIntValue(
				"imageloader.disabled-opacity", -1);
	}

	private Image loadImage(Display display, String key) {
		return loadImage(display, skinProperties.getStringValue(key), key);
	}

	private Image[] findResources(String sKey) {
		if (Collections.binarySearch(notFound, sKey) >= 0) {
			return null;
		}

		for (int i = 0; i < sSuffixChecks.length; i++) {
			String sSuffix = sSuffixChecks[i];

			if (sKey.endsWith(sSuffix)) {
				//System.out.println("YAY " + sSuffix + " for " + sKey);
				String sParentName = sKey.substring(0, sKey.length() - sSuffix.length());
				String[] sParentFiles = skinProperties.getStringArray(sParentName);
				if (sParentFiles != null) {
					boolean bFoundOne = false;
					Image[] images = parseValuesString(sKey, sParentFiles, sSuffix);
					if (images != null) {
  					for (int j = 0; j < images.length; j++) {
  						Image image = images[j];
  						if (isRealImage(image)) {
  							bFoundOne = true;
  						}
  					}
  					if (!bFoundOne) {
  						for (int j = 0; j < images.length; j++) {
  							Image image = images[j];
  							if (isRealImage(image)) {
  								image.dispose();
  							}
  						}
  					} else {
  						return images;
  					}
					}
				} else {
					// maybe there's another suffix..
					Image[] images = findResources(sParentName);
					if (images != null) {
						return images;
					}
				}
			}
		}

		int i = Collections.binarySearch(notFound, sKey) * -1 - 1;
		if (i >= 0) {
			notFound.add(i, sKey);
		}
		return null;
	}

	/**
	 * @param values
	 * @param suffix 
	 * @return
	 *
	 * @since 3.1.1.1
	 */
	private Image[] parseValuesString(String sKey, String[] values, String suffix) {
		Image[] images = null;
		
		int splitX = 0;
		int locationStart = 0;
		if (values[0].equals("multi") && values.length > 2) {
			splitX = Integer.parseInt(values[1]);
			locationStart = 2;
		}

		if (locationStart == 0 || splitX <= 0) {
			images = new Image[values.length];
			for (int i = 0; i < values.length; i++) {
				int index = values[i].lastIndexOf('.');
				if (index > 0) {
					String sTryFile = values[i].substring(0, index) + suffix
							+ values[i].substring(index);
					images[i] = loadImage(display, sTryFile, sKey);

					if (images[i] == null) {
						sTryFile = values[i].substring(0, index)
								+ suffix.replace('-', '_')
								+ values[i].substring(index);
						images[i] = loadImage(display, sTryFile, sKey);
					}
				}
				
				if (images[i] == null) {
					images[i] = getNoImage();
				}
			}
		} else {
			Image image = null;
			String origFile = values[locationStart];
			int index = origFile.lastIndexOf('.');
			if (index > 0) {
				String sTryFile = origFile.substring(0, index) + suffix
						+ origFile.substring(index);
				image = loadImage(display, sTryFile, sKey);

				if (image == null) {
					sTryFile = origFile.substring(0, index)
							+ suffix.replace('-', '_')
							+ origFile.substring(index);
					image = loadImage(display, sTryFile, sKey);
				}
			}
			if (image == null) {
				image = loadImage(display, values[locationStart], sKey);
			}
			
			if (image != null) {
  			Rectangle bounds = image.getBounds();
  			images = new Image[(bounds.width + splitX - 1) / splitX];
  			for (int i = 0; i < images.length; i++) {
  				Image imgBG = Utils.createAlphaImage(display, splitX,
  						bounds.height, (byte) 0);
  				images[i] = Utils.blitImage(display, image, new Rectangle(i * splitX,
  					0, splitX, bounds.height), imgBG, new Point(0, 0));
  				imgBG.dispose();
  			}
			}
		}
		
		return images;
	}

	private Image loadImage(Display display, String res, String sKey) {
		Image img = null;

		//System.out.println("LoadImage " + sKey + " - " + res);
		if (res == null) {
			for (int i = 0; i < sSuffixChecks.length; i++) {
				String sSuffix = sSuffixChecks[i];

				if (sKey.endsWith(sSuffix)) {
					//System.out.println("Yay " + sSuffix + " for " + sKey);
					String sParentName = sKey.substring(0, sKey.length()
							- sSuffix.length());
					String sParentFile = skinProperties.getStringValue(sParentName);
					if (sParentFile != null) {
						int index = sParentFile.lastIndexOf('.');
						if (index > 0) {
							String sTryFile = sParentFile.substring(0, index) + sSuffix
									+ sParentFile.substring(index);
							img = loadImage(display, sTryFile, sKey);

							if (img != null) {
								break;
							}

							sTryFile = sParentFile.substring(0, index)
									+ sSuffix.replace('-', '_') + sParentFile.substring(index);
							img = loadImage(display, sTryFile, sKey);

							if (img != null) {
								break;
							}
						}
					}
				}
			}
		}

		if (img == null) {
			try {
				InputStream is = classLoader.getResourceAsStream(res);
				if (is != null) {
					img = new Image(display, is);
				}

				if (img == null) {
					if (sKey.endsWith("-disabled") || sKey.endsWith("_disabled")) {
						Image imgToFade = getImage(sKey.substring(0, sKey.length() - 9));
						if (isRealImage(imgToFade)) {
							ImageData imageData = imgToFade.getImageData();
							// decrease alpha
							if (imageData.alphaData != null) {
								if (disabledOpacity == -1) {
									for (int i = 0; i < imageData.alphaData.length; i++) {
										imageData.alphaData[i] = (byte) ((imageData.alphaData[i] & 0xff) >> 3);
									}
								} else {
									for (int i = 0; i < imageData.alphaData.length; i++) {
										imageData.alphaData[i] = (byte) ((imageData.alphaData[i] & 0xff)
												* disabledOpacity / 100);
									}
								}
								img = new Image(display, imageData);
							} else {
								Rectangle bounds = imgToFade.getBounds();
								Image bg = Utils.createAlphaImage(display, bounds.width,
										bounds.height, (byte) 0);

								img = Utils.renderTransparency(display, bg, imgToFade,
										new Point(0, 0), disabledOpacity == -1 ? 64
												: disabledOpacity * 255 / 100);
								bg.dispose();
							}
						}
					}
					//System.err.println("ImageRepository:loadImage:: Resource not found: " + res);
				}
			} catch (Throwable e) {
				System.err.println("ImageRepository:loadImage:: Resource not found: "
						+ res + "\n" + e);
			}
		}

		return img;
	}

	public void unLoadImages() {
		Iterator iter;
		if (DEBUG_UNLOAD) {
			iter = mapImages.keySet().iterator();
			while (iter.hasNext()) {
				Object key = iter.next();
				Image[] images = (Image[]) mapImages.get(key);
				if (images != null) {
					for (int i = 0; i < images.length; i++) {
						Image image = images[i];
						if (image != null && !image.isDisposed()) {
							System.out.println("dispose " + image + ";" + key);
							image.dispose();
						}
					}
				}
			}
		} else {
			iter = mapImages.values().iterator();
			while (iter.hasNext()) {
				Image[] images = (Image[]) iter.next();
				if (images != null) {
					for (int i = 0; i < images.length; i++) {
						Image image = images[i];
						if (image != null && !image.isDisposed()) {
							image.dispose();
						}
					}
				}
			}
		}
	}

	public Image[] getImages(String sKey) {
		if (sKey == null) {
			return new Image[0];
		}

		Image[] images = (Image[]) mapImages.get(sKey);

		if (images != null) {
			return images;
		}
		String[] locations = skinProperties.getStringArray(sKey);
		//		System.out.println(sKey + "=" + properties.getStringValue(sKey)
		//				+ ";" + ((locations == null) ? "null" : "" + locations.length));
		if (locations == null || locations.length == 0) {
			images = findResources(sKey);

			if (images == null) {
				images = new Image[0];
			}

			for (int i = 0; i < images.length; i++) {
				if (images[i] == null) {
					images[i] = getNoImage();
				}
			}
		} else {
			images = parseValuesString(sKey, locations, "");
		}

		mapImages.put(sKey, images);

		return images;
	}

	public Image getImage(String sKey) {
		Image[] images = getImages(sKey);
		if (images == null || images.length == 0) {
			return getNoImage();
		}
		return images[0];
	}

	private static Image getNoImage() {
		if (noImage == null) {
			Display display = Display.getDefault();
			final int SIZE = 10;
			noImage = new Image(display, SIZE, SIZE);
			GC gc = new GC(noImage);
			gc.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
			gc.fillRectangle(0, 0, SIZE, SIZE);
			gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
			gc.drawRectangle(0, 0, SIZE - 1, SIZE - 1);
			gc.dispose();
		}
		return noImage;
	}

	public boolean imageExists(String name) {
		return isRealImage(getImage(name));
	}

	public static boolean isRealImage(Image image) {
		return image != null && image != getNoImage() && !image.isDisposed();
	}
	
	public int getAnimationDelay(String sKey) {
		return skinProperties.getIntValue(sKey + ".delay", 100);
	}
}
