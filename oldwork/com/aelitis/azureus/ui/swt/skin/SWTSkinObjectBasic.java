/**
 * 
 */
package com.aelitis.azureus.ui.swt.skin;

import java.util.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.*;

import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.core3.util.AEMonitor;
import org.gudy.azureus2.core3.util.AERunnable;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.ui.swt.Utils;

import com.aelitis.azureus.ui.swt.utils.ColorCache;
import com.aelitis.azureus.ui.swt.utils.ImageLoader;
import com.aelitis.azureus.util.StringCompareUtils;

/**
 * @author TuxPaper
 * @created Jun 12, 2006
 *
 */
public class SWTSkinObjectBasic
	implements SWTSkinObject, PaintListener
{
	protected static final int BORDER_ROUNDED = 1;

	protected static final int BORDER_ROUNDED_FILL = 2;

	protected static final int BORDER_GRADIENT = 3;

	protected Control control;

	protected String type;

	protected String sConfigID;

	protected SWTBGImagePainter painter;

	protected SWTSkinProperties properties;

	protected String sID;

	// XXX Might be wise to force this to SWTSkinObjectContainer
	protected SWTSkinObject parent;

	protected SWTSkin skin;

	protected String[] suffixes = null;

	protected ArrayList listeners = new ArrayList();

	protected AEMonitor listeners_mon = new AEMonitor(
			"SWTSkinObjectBasic::listener");

	private String sViewID;

	private boolean isVisible;

	protected Color bgColor;

	private Color colorBorder;

	private int[] colorBorderParams = null;

	private int[] colorFillParams;

	private int colorFillType;

	private boolean initialized = false;

	boolean paintListenerHooked = false;

	boolean alwaysHookPaintListener = false;
	
	private Map mapData = Collections.EMPTY_MAP;
	
	private boolean disposed = false;
	
	protected boolean debug = false;

	protected Color bgColor2;

	protected int bgColor2until;

	private Image bgImage;

	private String tooltipID;

	protected boolean customTooltipID = false;

	private Listener resizeBGListener;

	/**
	 * @param properties TODO
	 * 
	 */
	public SWTSkinObjectBasic(SWTSkin skin, SWTSkinProperties properties,
			Control control, String sID, String sConfigID, String type,
			SWTSkinObject parent) {
		this(skin, properties, sID, sConfigID, type, parent);
		setControl(control);
		
	}

	public SWTSkinObjectBasic(SWTSkin skin, SWTSkinProperties properties,
			String sID, String sConfigID, String type, SWTSkinObject parent) {
		this.skin = skin;
		this.properties = properties;
		this.sConfigID = sConfigID;
		this.sID = sID;
		this.type = type;
		this.parent = parent;
		setViewID(properties.getStringValue(sConfigID + ".view"));
		setDebug(properties.getBooleanValue(sConfigID + ".debug", false));
	}

	public void setControl(final Control control) {
		if (!Utils.isThisThreadSWT()) {
			Debug.out("Warning: setControl not called in SWT thread for " + this);
			Utils.execSWTThread(new AERunnable() {
				public void runSupport() {
					setControl(control);
				}
			});
			return;
		}
		
		resizeBGListener = new Listener() {
			public void handleEvent(Event event) {
				if (bgImage != null && !bgImage.isDisposed()) {
					bgImage.dispose();
				}
				Rectangle bounds = control.getBounds();
				if (bounds.height <= 0) {
					return;
				}
				bgImage = new Image(control.getDisplay(), 5, bounds.height);
				GC gc = new GC(bgImage);
				try {
					try {
						gc.setAdvanced(true);
						gc.setInterpolation(SWT.HIGH);
						gc.setAntialias(SWT.ON);
					} catch (Exception ex) {
					}
					gc.setBackground(bgColor);
					gc.setForeground(bgColor2);
					gc.fillGradientRectangle(0, 0, 5, bgColor2until > 0 ? bgColor2until : bounds.height, true);
					if (bgColor2until > 0) {
						//gc.setBackground(bgColor2);
						gc.fillRectangle(0, bgColor2until, 5, bounds.height - bgColor2until);
					}
				} finally {
					gc.dispose();
				}
				if (painter == null) {
					painter = new SWTBGImagePainter(control, null, null,
							bgImage, SWTSkinUtils.TILE_X);
				} else {
					painter.setImage(null, null, bgImage);
				}
			}
		};

		this.control = control;
		control.setData("ConfigID", sConfigID);
		control.setData("SkinObject", this);

		SWTSkinUtils.addMouseImageChangeListeners(control);
		switchSuffix("", 1, false);

		// setvisible is one time only
		if (!properties.getBooleanValue(sConfigID + ".visible", true)) {
			setVisible(false);
		}

		final Listener lShowHide = new Listener() {
			public void handleEvent(final Event event) {
				final boolean toBeVisible = event.type == SWT.Show;
				if (toBeVisible == control.isVisible() && isVisible == toBeVisible) {
					return;
				}

				//System.out.println(SWTSkinObjectBasic.this + ">show/hide " + ((event.widget).getData("SkinObject")) + ";" + ((Control)event.widget).isVisible() + ";" + Debug.getCompressedStackTrace());
				// wait until show or hide event is processed to guarantee
				// isVisible will be correct for listener triggers
				Utils.execSWTThreadLater(0, new AERunnable() {
					public void runSupport() {
						//System.out.println(">>show/hide " + ((event.widget).getData("SkinObject")) + ";" + ((Control)event.widget).isVisible());
						if (control == null || control.isDisposed()) {
							setIsVisible(false);
							return;
						}

						if (event.widget == control) {
							setIsVisible(toBeVisible);
							return;
						}

						if (!toBeVisible || control.isVisible()) {
							setIsVisible(toBeVisible);
							return;
						}
						setIsVisible(control.isVisible());
					}
				});
			}
		};
		setIsVisible(control.isVisible());

		final ArrayList listenersToRemove = new ArrayList(2);
		Control walkUp = control;
		do {
			listenersToRemove.add(walkUp);
			walkUp.addListener(SWT.Show, lShowHide);
			walkUp.addListener(SWT.Hide, lShowHide);
			walkUp = walkUp.getParent();
		} while (walkUp != null);

		control.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				disposed = true;
				for (Iterator iter = listenersToRemove.iterator(); iter.hasNext();) {
					Control control = (Control) iter.next();
					if (control != null && !control.isDisposed()) {
						control.removeListener(SWT.Show, lShowHide);
						control.removeListener(SWT.Hide, lShowHide);
					}
				}
				skin.removeSkinObject(SWTSkinObjectBasic.this);
			}
		});
		
		control.addListener(SWT.MouseHover, new Listener() {
			public void handleEvent(Event event) {
				String id = getTooltipID(true);
				if (id == null) {
					control.setToolTipText(null);
				} else {
					control.setToolTipText(MessageText.getString(id, (String) null));
				}
			}
		});
		
		if (skin.isLayoutComplete()) {
			skin.attachControl(this);
		}
	}

	/**
	 * @param visible
	 *
	 * @since 3.0.4.3
	 */
	protected void setIsVisible(boolean visible) {
		if (visible == isVisible) {
			return;
		}
		isVisible = visible;
		switchSuffix(null, 0, false);
		triggerListeners(visible ? SWTSkinObjectListener.EVENT_SHOW
				: SWTSkinObjectListener.EVENT_HIDE);
	}

	public Control getControl() {
		return control;
	}

	public String getType() {
		return type;
	}

	public String getConfigID() {
		return sConfigID;
	}

	public String getSkinObjectID() {
		return sID;
	}

	// @see com.aelitis.azureus.ui.swt.skin.SWTSkinObject#getParent()
	public SWTSkinObject getParent() {
		return parent;
	}

	public void setBackground(String sConfigID, String sSuffix) {
		Image imageBG;
		Image imageBGLeft;
		Image imageBGRight;

		if (sConfigID == null) {
			return;
		}

		ImageLoader imageLoader = skin.getImageLoader(properties);
		String s = properties.getStringValue(sConfigID + sSuffix, (String) null);
		if (s != null && s.length() > 0) {
			Image[] images = imageLoader.getImages(sConfigID + sSuffix);
			if (images.length == 1 && ImageLoader.isRealImage(images[0])) {
				imageBG = images[0];
				imageBGLeft = imageLoader.getImage(sConfigID + sSuffix + "-left");
				imageBGRight = imageLoader.getImage(sConfigID + sSuffix + "-right");
			} else if (images.length == 3 && ImageLoader.isRealImage(images[2])) {
				imageBGLeft = images[0];
				imageBG = images[1];
				imageBGRight = images[2];
			} else if (images.length == 2 && ImageLoader.isRealImage(images[1])) {
				imageBGLeft = images[0];
				imageBG = images[1];
				imageBGRight = imageLoader.getImage(sConfigID + sSuffix + "-right");
			} else {
				//if (sSuffix.length() > 0) {
				//	setBackground(sConfigID, "");
				//}
				return;
			}
		} else {
			if (s != null && painter != null) {
				painter.dispose();
				painter = null;
			}
			if (s == null) {
				//if (sSuffix.length() > 0) {
				//	setBackground(sConfigID, "");
				//}
			}
			return;
		}

		if (painter == null) {
			//control.setBackgroundImage doesn't handle transparency!
			//control.setBackgroundImage(image);

			// Workaround: create our own image with shell's background
			// for "transparent" area.  Doesn't allow control's image to show
			// through.  To do that, we'd have to walk up the tree until we 
			// found a composite with an image
			//control.setBackgroundMode(SWT.INHERIT_NONE);
			//control.setBackgroundImage(imageBG);

			String sTileMode = properties.getStringValue(sConfigID + ".drawmode");
			int tileMode = SWTSkinUtils.getTileMode(sTileMode);
			painter = new SWTBGImagePainter(control, imageBGLeft, imageBGRight,
					imageBG, tileMode);
		} else {
			//System.out.println("setImage " + sConfigID + "  " + sSuffix);
			painter.setImage(imageBGLeft, imageBGRight, imageBG);
		}

		// XXX Is this needed?  It causes flicker and slows things down.
		//     Maybe a redraw instead (if anything at all)?
		//control.update();
	}

	// @see java.lang.Object#toString()
	public String toString() {
		String s = "SWTSkinObjectBasic {" + sID;

		if (!sID.equals(sConfigID)) {
			s += "/" + sConfigID;
		}

		if (sViewID != null) {
			s += "/v=" + sViewID;
		}

		s += ", " + type + "; parent="
				+ ((parent == null) ? null : parent.getSkinObjectID() + "}");

		return s;
	}

	// @see com.aelitis.azureus.ui.swt.skin.SWTSkinObject#getSkin()
	public SWTSkin getSkin() {
		return skin;
	}

	// @see java.lang.Object#equals(java.lang.Object)
	public boolean equals(Object obj) {
		if (obj instanceof SWTSkinObject) {
			SWTSkinObject skinObject = (SWTSkinObject) obj;
			boolean bEquals = skinObject.getSkinObjectID().equals(sID);
			if (parent != null) {
				return bEquals && parent.equals(skinObject.getParent());
			}
			return bEquals;
		}

		return super.equals(obj);
	}

	// @see com.aelitis.azureus.ui.swt.skin.SWTSkinObject#setVisible(boolean)
	public void setVisible(final boolean visible) {
		Utils.execSWTThread(new AERunnable() {
			public void runSupport() {
				if (control != null && !control.isDisposed()) {
					Object ld = control.getLayoutData();
					if (ld instanceof FormData) {
						FormData fd = (FormData) ld;
						if (!visible) {
							if (fd.width != 0 && fd.height != 0) {
								control.setData("oldSize", new Point(fd.width, fd.height));
							}
							fd.width = 0;
							fd.height = 0;
						} else {
							Object oldSize = control.getData("oldSize");
							Point oldSizePoint = (oldSize instanceof Point) ? (Point) oldSize
									: new Point(SWT.DEFAULT, SWT.DEFAULT);
							if (fd.width <= 0) {
								fd.width = oldSizePoint.x;
							}
							if (fd.height <= 0) {
								fd.height = oldSizePoint.y;
							}
						}
						control.setLayoutData(fd);
						control.getParent().layout(true);
						Utils.relayout(control);
					}
					control.setVisible(visible);
					setIsVisible(visible);
				}
			}
		});
	}

	// @see com.aelitis.azureus.ui.swt.skin.SWTSkinObject#setDefaultVisibility()
	public void setDefaultVisibility() {
		if (sConfigID == null) {
			return;
		}

		setVisible(getDefaultVisibility());
	}

	public boolean getDefaultVisibility() {
		return properties.getBooleanValue(sConfigID + ".visible", true);
	}

	public boolean isVisible() {
		if (control == null || control.isDisposed()) {
			return false;
		}
		return isVisible;
	}

	/**
	 * Switch the suffix using the default of <code>1</code> for level and <code>false</code> for walkUp 
	 */
	public String switchSuffix(String suffix) {
		return switchSuffix(suffix, 1, false);
	}

	public final String switchSuffix(String suffix, int level, boolean walkUp) {
		return switchSuffix(suffix, level, walkUp, true);
	}

	public String switchSuffix(String suffix, int level, boolean walkUp, boolean walkDown) {
		if (walkUp) {
			SWTSkinObject parentSkinObject = parent;
			SWTSkinObject skinObject = this;

			// Move up the tree until propogation stops
			while ((parentSkinObject instanceof SWTSkinObjectContainer)
					&& ((SWTSkinObjectContainer) parentSkinObject).getPropogation()) {
				skinObject = parentSkinObject;
				parentSkinObject = parentSkinObject.getParent();
			}

			if (skinObject != this) {
				//System.out.println(sConfigID + suffix + "; walkup");

				skinObject.switchSuffix(suffix, level, false);
				return null;
			}
		}

		if (level > 0) {
  		//System.out.println(SystemTime.getCurrentTime() + ": " + this + suffix + "; switchy");
  		if (suffixes == null) {
  			suffixes = new String[level];
  		} else if (suffixes.length < level) {
  			String[] newSuffixes = new String[level];
  			System.arraycopy(suffixes, 0, newSuffixes, 0, suffixes.length);
  			suffixes = newSuffixes;
  		}
  		suffixes[level - 1] = suffix;
		}

		suffix = getSuffix();

		if (sConfigID == null || control == null || control.isDisposed() || !isVisible) {
			return suffix;
		}

		final String sSuffix = suffix;

		Utils.execSWTThread(new AERunnable() {

			public void runSupport() {
				if (control == null || control.isDisposed()) {
					return;
				}

				control.removeListener(SWT.Resize, resizeBGListener);

				boolean needPaintHook = false;

				if (properties.hasKey(sConfigID + ".color" + sSuffix)) {
					Color color = properties.getColor(sConfigID + ".color" + sSuffix);
					bgColor = color;
					String colorStyle = properties.getStringValue(sConfigID
							+ ".color.style" + sSuffix);
					if (colorStyle != null) {
						String[] split = colorStyle.split(",");

						if (split.length > 2) {
							try {
  							colorFillParams = new int[] {
  								Integer.parseInt(split[1]),
  								Integer.parseInt(split[2])
  							};
							} catch (NumberFormatException e) {
								//ignore
							}
						}

						if (split[0].equals("rounded")) {
							colorFillType = BORDER_ROUNDED;
							needPaintHook = true;
						} else if (split[0].equals("rounded-fill")) {
							colorFillType = BORDER_ROUNDED_FILL;
							needPaintHook = true;
						} else if (split[0].equals("gradient")) {
							colorFillType = BORDER_GRADIENT;
							bgColor2 = ColorCache.getColor(Display.getDefault(), split[1]);
							if (split.length > 2) {
								bgColor2until = Integer.parseInt(split[2]);
							}

							control.addListener(SWT.Resize, resizeBGListener);
							resizeBGListener.handleEvent(null);
						}

						control.redraw();
						control.setBackground(null);
					} else {
						control.setBackground(bgColor);
					}
				}

				if (properties.hasKey(sConfigID + ".fgcolor" + sSuffix)) {
  				Color fg = properties.getColor(sConfigID + ".fgcolor" + sSuffix);
 					control.setForeground(fg);
				}

				// Color,[width]
				String sBorderStyle = properties.getStringValue(sConfigID + ".border"
						+ sSuffix);
				colorBorder = null;
				colorBorderParams = null;
				if (sBorderStyle != null) {
					String[] split = sBorderStyle.split(",");
					colorBorder = ColorCache.getColor(control.getDisplay(), split[0]);
					needPaintHook |= colorBorder != null;

					if (split.length > 2) {
						colorBorderParams = new int[] {
							Integer.parseInt(split[1]),
							Integer.parseInt(split[2])
						};
					}
				}

				setBackground(sConfigID + ".background", sSuffix);

				String sCursor = properties.getStringValue(sConfigID + ".cursor");
				if (sCursor != null && sCursor.length() > 0) {
					if (sCursor.equalsIgnoreCase("hand")) {
						Listener handCursorListener = skin.getHandCursorListener(control.getDisplay());
						control.removeListener(SWT.MouseEnter, handCursorListener);
						control.removeListener(SWT.MouseExit, handCursorListener);
						
						control.addListener(SWT.MouseEnter, handCursorListener);
						control.addListener(SWT.MouseExit, handCursorListener);
					}
				}

				if (!customTooltipID ) {
  				String newToolTipID = properties.getReferenceID(sConfigID + ".tooltip"
  						+ sSuffix);
  				if (newToolTipID == null && sSuffix.length() > 0) {
  					newToolTipID = properties.getReferenceID(sConfigID + ".tooltip");
  				}
  				tooltipID = newToolTipID;
				}
				
				if (!alwaysHookPaintListener && needPaintHook != paintListenerHooked) {
					if (needPaintHook) {
						control.addPaintListener(SWTSkinObjectBasic.this);
					} else {
						control.removePaintListener(SWTSkinObjectBasic.this);
					}
					paintListenerHooked = needPaintHook;
				}

			}

		});
		return suffix;
	}

	public String getSuffix() {
		String suffix = "";
		for (int i = 0; i < suffixes.length; i++) {
			if (suffixes[i] != null) {
				suffix += suffixes[i];
			}
		}
		if (suffix.indexOf("-down-over") >= 0) {
			return suffix.replaceAll("-down-over", "-down");
		}
		return suffix;
	}

	/**
	 * @return the properties
	 */
	public SWTSkinProperties getProperties() {
		return properties;
	}

	public void setProperties(SWTSkinProperties skinProperties) {
		this.properties = skinProperties;
	}

	/* (non-Javadoc)
	 * @see com.aelitis.azureus.ui.swt.skin.SWTSkinObject#addListener(com.aelitis.azureus.ui.swt.skin.SWTSkinObjectListener)
	 */
	public void addListener(final SWTSkinObjectListener listener) {
		listeners_mon.enter();
		try {
			listeners.add(listener);
		} finally {
			listeners_mon.exit();
		}
		
		if (initialized) {
			listener.eventOccured(this, SWTSkinObjectListener.EVENT_CREATED, null);
		}

		if (isVisible && initialized) {
			Utils.execSWTThread(new AERunnable() {
				public void runSupport() {
					listener.eventOccured(SWTSkinObjectBasic.this,
							SWTSkinObjectListener.EVENT_SHOW, null);
				}
			});
		}
	}

	/* (non-Javadoc)
	 * @see com.aelitis.azureus.ui.swt.skin.SWTSkinObject#removeListener(com.aelitis.azureus.ui.swt.skin.SWTSkinObjectListener)
	 */
	public void removeListener(SWTSkinObjectListener listener) {
		listeners_mon.enter();
		try {
			listeners.remove(listener);
		} finally {
			listeners_mon.exit();
		}
	}

	public SWTSkinObjectListener[] getListeners() {
		return (SWTSkinObjectListener[]) listeners.toArray(new SWTSkinObjectListener[0]);
	}

	public void triggerListeners(int eventType) {
		triggerListeners(eventType, null);
	}

	public void triggerListeners(final int eventType, final Object params) {
		// delay show and hide events while not initialized
		if (eventType == SWTSkinObjectListener.EVENT_SHOW
				|| eventType == SWTSkinObjectListener.EVENT_HIDE) {
			if (!initialized) {
				return;
			}

			if (eventType == SWTSkinObjectListener.EVENT_SHOW && !isVisible) {
				return;
			} else if (eventType == SWTSkinObjectListener.EVENT_HIDE && isVisible) {
				return;
			}
		} else if (eventType == SWTSkinObjectListener.EVENT_CREATED) {
			initialized = true;
		}

		// process listeners added locally
		if (listeners.size() > 0) {
  		listeners_mon.enter();
  		try {
  			for (Iterator iterator = listeners.iterator(); iterator.hasNext();) {
  				try {
  					SWTSkinObjectListener l = (SWTSkinObjectListener) iterator.next();
  					l.eventOccured(this, eventType, params);
  				} catch (Exception e) {
  					Debug.out("Skin Event " + SWTSkinObjectListener.NAMES[eventType]
  							+ " caused an error for listener added locally", e);
  				}
  			}
  		} finally {
  			listeners_mon.exit();
  		}
		}

		// process listeners added to skin
		SWTSkinObjectListener[] listeners = skin.getSkinObjectListeners(sViewID);
		if (listeners.length > 0) {
  		for (int i = 0; i < listeners.length; i++) {
  			try {
  				SWTSkinObjectListener l = listeners[i];
  				l.eventOccured(this, eventType, params);
  			} catch (Exception e) {
  				Debug.out("Skin Event " + SWTSkinObjectListener.NAMES[eventType]
  						+ " caused an error for listener added to skin", e);
  			}
  		}
		}

		if (eventType == SWTSkinObjectListener.EVENT_CREATED) {
			triggerListeners(isVisible ? SWTSkinObjectListener.EVENT_SHOW
					: SWTSkinObjectListener.EVENT_HIDE);
		}
	}

	protected void setViewID(String viewID) {
		sViewID = viewID;
	}

	public String getViewID() {
		return sViewID;
	}

	// @see com.aelitis.azureus.ui.swt.skin.SWTSkinObject#dispose()
	public void dispose() {
		if (disposed) {
			return;
		}
		if (control != null && !control.isDisposed()) {
			control.dispose();
		}
	}

	public boolean isDisposed() {
		return disposed;
	}

	// @see com.aelitis.azureus.ui.swt.skin.SWTSkinObject#setTooltipID(java.lang.String)
	public void setTooltipID(final String id) {
		if (isDisposed()) {
			return;
		}
		if (StringCompareUtils.equals(id, tooltipID)) {
			return;
		}

		tooltipID = id;
		customTooltipID = true;
	}
	
	// @see com.aelitis.azureus.ui.swt.skin.SWTSkinObject#getTooltipID(boolean)
	public String getTooltipID(boolean walkup) {
		if (tooltipID != null || !walkup) {
			return tooltipID;
		}
		if (parent != null) {
			return parent.getTooltipID(true);
		}
		return null;
	}

	public void paintControl(GC gc) {
	}

	// @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
	public final void paintControl(PaintEvent e) {
		if (bgColor != null) {
			e.gc.setBackground(bgColor);
		}

		paintControl(e.gc);

		try {
			e.gc.setAdvanced(true);
			e.gc.setAntialias(SWT.ON);
		} catch (Exception ex) {
		}

		if (colorFillType > 0) {

			Rectangle bounds = (control instanceof Composite)
					? ((Composite) control).getClientArea() : control.getBounds();
			if (colorFillParams != null) {
  			if (colorFillType == BORDER_ROUNDED_FILL) {
  				e.gc.fillRoundRectangle(0, 0, bounds.width, bounds.height, colorFillParams[0], colorFillParams[1]);
  			} else if (colorFillType == BORDER_ROUNDED) {
  				Color oldFG = e.gc.getForeground();
  				e.gc.setForeground(bgColor);
  				e.gc.drawRoundRectangle(0, 0, bounds.width - 1, bounds.height - 1, colorFillParams[0],
  						colorFillParams[1]);
  				e.gc.setForeground(oldFG);
  			}
			}
//			if (colorFillType == BORDER_GRADIENT) {
//				Color oldFG = e.gc.getForeground();
//				e.gc.setForeground(bgColor2);
//				e.gc.fillGradientRectangle(0, 0, bounds.width - 1, bounds.height - 1, true);
//				e.gc.setForeground(oldFG);
//			}			
		}

		if (colorBorder != null) {
			e.gc.setForeground(colorBorder);
			Rectangle bounds = (control instanceof Composite)
					? ((Composite) control).getClientArea() : control.getBounds();
			bounds.width -= 1;
			bounds.height -= 1;
			if (colorBorderParams == null) {
				e.gc.drawRectangle(bounds);
			} else {
				e.gc.drawRoundRectangle(bounds.x, bounds.y, bounds.width,
						bounds.height, colorBorderParams[0], colorBorderParams[1]);
			}
		}
	}

	public boolean isAlwaysHookPaintListener() {
		return alwaysHookPaintListener;
	}

	public void setAlwaysHookPaintListener(boolean alwaysHookPaintListener) {
		this.alwaysHookPaintListener = alwaysHookPaintListener;
		if (alwaysHookPaintListener && !paintListenerHooked) {
			control.addPaintListener(SWTSkinObjectBasic.this);
			paintListenerHooked = true;
		}
	}

	// @see com.aelitis.azureus.ui.swt.skin.SWTSkinObject#getData(java.lang.String)
	public Object getData(String id) {
		return mapData.get(id);
	}

	// @see com.aelitis.azureus.ui.swt.skin.SWTSkinObject#setData(java.lang.String, java.lang.Object)
	public void setData(String id, Object data) {
		if (mapData == Collections.EMPTY_MAP) {
			mapData = new HashMap(1);
		}
		mapData.put(id, data);
	}

	// @see org.gudy.azureus2.ui.swt.debug.ObfusticateShell#generateObfusticatedImage()
	public Image generateObfusticatedImage() {
		return null;
	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return debug;
	}
}
