package com.aelitis.azureus.ui.swt.views.skin.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.gudy.azureus2.core3.util.AERunnable;
import org.gudy.azureus2.ui.swt.ImageRepository;
import org.gudy.azureus2.ui.swt.Utils;
import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;

import com.aelitis.azureus.buddy.VuzeBuddy;
import com.aelitis.azureus.ui.swt.skin.SWTSkinFactory;
import com.aelitis.azureus.ui.swt.views.skin.BuddiesViewer;

public class FriendsList
{

	private Composite content;

	private ScrolledComposite scrollable;

	private Canvas canvas;

	private List friendsWidgets = new ArrayList();

	private Color widgetBackgroundColor;

	private Color borderColor;
//
//	private Color normalColor;

	private boolean isEmailDisplayOnly = false;

	private BuddiesViewer buddiesViewer;

	private Image default_prompt_image = null;

	private String default_prompt_text = null;

	private Rectangle textBounds;

//	private Color textColor;

	public FriendsList(Composite parent) {
		content = new Composite(parent, SWT.NONE);
		FillLayout fLayout = new FillLayout();
		fLayout.marginHeight = 4;
		fLayout.marginWidth = 4;
		content.setLayout(fLayout);
		content.setBackgroundMode(SWT.INHERIT_DEFAULT);

		scrollable = new ScrolledComposite(content, SWT.V_SCROLL | SWT.NONE);
		scrollable.setExpandHorizontal(true);
		scrollable.setExpandVertical(true);
		scrollable.setBackgroundMode(SWT.INHERIT_DEFAULT);

		canvas = new Canvas(scrollable, SWT.DOUBLE_BUFFERED);
		borderColor = SWTSkinFactory.getInstance().getSkinProperties().getColor(
				"color.widget.border");
//		normalColor = SWTSkinFactory.getInstance().getSkinProperties().getColor(
//				"color.table.bg");
//		textColor = SWTSkinFactory.getInstance().getSkinProperties().getColor(
//				"color.text.fg");
//
//		widgetBackgroundColor = SWTSkinFactory.getInstance().getSkinProperties().getColor(
//				"color.widget.container.bg");
		widgetBackgroundColor = parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
		scrollable.setContent(canvas);

		init();
	}

	private void init() {

		canvas.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				textBounds = canvas.getClientArea();

				try {
					e.gc.setAntialias(SWT.ON);
					e.gc.setTextAntialias(SWT.ON);
					e.gc.setInterpolation(SWT.HIGH);
				} catch (Exception ex) {
					// ignore.. some of these may not be avail
				}

				Rectangle bounds = canvas.getBounds();
				bounds.width -= 1;
				bounds.height -= 1;
//				if (normalColor != null) {
//					e.gc.setBackground(normalColor);
//				}
				e.gc.fillRectangle(bounds);
				if (borderColor != null) {
					e.gc.setForeground(borderColor);
					e.gc.drawRectangle(bounds);
				}

				if (friendsWidgets.size() < 1) {
//					if (textColor != null) {
//						e.gc.setForeground(textColor);
//					}
//					int imageXOffset = 16;
//					if (null != default_prompt_image
//							&& false == default_prompt_image.isDisposed()) {
//						Rectangle imageBounds = default_prompt_image.getBounds();
//						e.gc.drawImage(default_prompt_image, imageXOffset,
//								(bounds.height / 2) - (imageBounds.height / 2));
//
//						textBounds.x += default_prompt_image.getBounds().width
//								+ imageXOffset + 8;
//						textBounds.width -= default_prompt_image.getBounds().width
//								+ imageXOffset + 8;
//					}

					textBounds.x += 8;
					textBounds.width -= 16;
					
					if (null != default_prompt_text && default_prompt_text.length() > 0) {
						GCStringPrinter.printString(e.gc, default_prompt_text, textBounds,
								false, false, SWT.WRAP | SWT.CENTER);
					}
				}
			}
		});

		canvas.addControlListener(new ControlListener() {

			public void controlResized(ControlEvent e) {
				Rectangle r = scrollable.getClientArea();
				scrollable.setMinSize(canvas.computeSize(r.width, SWT.DEFAULT));
			}

			public void controlMoved(ControlEvent e) {
				// TODO Auto-generated method stub

			}
		});

		canvas.setBackground(widgetBackgroundColor);

		GridLayout gLayout = new GridLayout();
		gLayout.marginWidth = 1;
		gLayout.marginHeight = 1;
		gLayout.verticalSpacing = 0;
		gLayout.horizontalSpacing = 0;

		canvas.setLayout(gLayout);

		content.layout(true, true);
	}

	public void addFriend(final VuzeBuddy buddy) {
		Utils.execSWTThread(new AERunnable() {
			public void runSupport() {
				if (null == findWidget(buddy)) {
					FriendWidget widget = new FriendWidget(canvas, buddy);
					Rectangle r = scrollable.getClientArea();

					friendsWidgets.add(widget);
					GridData gData = new GridData(GridData.FILL_HORIZONTAL);
					gData.heightHint = 22;
					widget.getControl().setLayoutData(gData);
					canvas.layout(true, true);
					scrollable.setMinSize(canvas.computeSize(r.width, SWT.DEFAULT));
					content.layout(true, true);
					canvas.redraw();
				}
			}
		});
	}

	public void removeFriend(final VuzeBuddy buddy) {
		Utils.execSWTThread(new AERunnable() {
			public void runSupport() {
				FriendWidget widget = findWidget(buddy);
				if (null != widget) {
					friendsWidgets.remove(widget);
					widget.dispose(false);
					canvas.layout(true);
					Rectangle r = scrollable.getClientArea();
					scrollable.setMinSize(canvas.computeSize(r.width, SWT.DEFAULT));
					canvas.redraw();
				}
			}
		});

	}

	public void clear() {
		for (Iterator iterator = friendsWidgets.iterator(); iterator.hasNext();) {
			FriendWidget widget = (FriendWidget) iterator.next();
			widget.dispose(false);
		}
		friendsWidgets.clear();
		Rectangle r = scrollable.getClientArea();
		scrollable.setMinSize(canvas.computeSize(r.width, SWT.DEFAULT));
	}

	public FriendWidget findWidget(VuzeBuddy buddy) {
		if (null != buddy) {
			for (Iterator iterator = friendsWidgets.iterator(); iterator.hasNext();) {
				FriendWidget widget = (FriendWidget) iterator.next();
				if (null != widget.getBuddy()
						&& widget.getBuddy().getLoginID().equals(buddy.getLoginID())) {
					return widget;
				}
			}
		}
		return null;

	}

	public Control getControl() {
		return content;
	}

	private class FriendWidget
	{
		private Composite parent;

		private Canvas friendCanvas;

		private VuzeBuddy buddy;

		private Image closeButton;

		private Image closeButton_over;

		private Rectangle closeButtonBounds;

		private Rectangle textAreaBounds;

		private Color normalColor = null;

		private Color borderColor = null;

		private boolean isActive = false;

		private boolean closeIsActive = false;

		private int alpha = 255;

		private Font boldFont = null;

		private Font normalFont = null;

		private FriendWidget(Composite parent, VuzeBuddy buddy) {
			this.parent = parent;
			this.buddy = buddy;

			if (null == ImageRepository.getImage("button_skin_close")) {
				ImageRepository.addPath(
						"com/aelitis/azureus/ui/images/button_skin_close.png",
						"button_skin_close");
				ImageRepository.addPath(
						"com/aelitis/azureus/ui/images/button_skin_close-over.png",
						"button_skin_close-over");
			}

			closeButton = ImageRepository.getImage("button_skin_close");
			closeButton_over = ImageRepository.getImage("button_skin_close-over");
			closeButtonBounds = new Rectangle(0, 0, closeButton.getImageData().width,
					closeButton.getImageData().height);

			//			activeColor = SWTSkinFactory.getInstance().getSkinProperties().getColor(
			//					"color.row.selected");
			normalColor = SWTSkinFactory.getInstance().getSkinProperties().getColor(
					"color.table.bg");

			borderColor = SWTSkinFactory.getInstance().getSkinProperties().getColor(
					"color.widget.border");

			friendCanvas = new Canvas(parent, SWT.DOUBLE_BUFFERED);
			friendCanvas.setBackgroundMode(SWT.INHERIT_DEFAULT);

			friendCanvas.addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent e) {
					Rectangle bounds = friendCanvas.getBounds();
					closeButtonBounds.x = bounds.width - closeButtonBounds.width - 6;
					closeButtonBounds.y = (bounds.height / 2)
							- (closeButtonBounds.height / 2);
					textAreaBounds = new Rectangle(6, 0, closeButtonBounds.x - 6,
							bounds.height);
				}
			});

			friendCanvas.addMouseTrackListener(new MouseTrackAdapter() {

				public void mouseEnter(MouseEvent e) {
					if (false == isEmailDisplayOnly()) {
						isActive = true;
						friendCanvas.redraw();
					}
				}

				public void mouseExit(MouseEvent e) {
					if (false == isEmailDisplayOnly()) {
						isActive = false;
						friendCanvas.redraw();
					}
				}

				public void mouseHover(MouseEvent e) {
					super.mouseHover(e);
				}

			});

			friendCanvas.addMouseListener(new MouseAdapter() {

				public void mouseUp(MouseEvent e) {
					if (false == isEmailDisplayOnly()) {
						if (true == closeButtonBounds.contains(e.x, e.y)) {
							if (true == closeIsActive) {
								//								removeFriend(FriendWidget.this.buddy);
								getBuddiesViewer().removeFromShare(FriendWidget.this.buddy);
							}
						}
					}
				}

			});
			friendCanvas.addMouseMoveListener(new MouseMoveListener() {

				public void mouseMove(MouseEvent e) {
					if (false == isEmailDisplayOnly()) {
						if (true == closeButtonBounds.contains(e.x, e.y)) {
							if (false == closeIsActive) {
								closeIsActive = true;
								friendCanvas.redraw();
							}
						} else {
							if (true == closeIsActive) {
								closeIsActive = false;
								friendCanvas.redraw();
							}
						}
					}
				}

			});
			friendCanvas.addPaintListener(new PaintListener() {

				public void paintControl(PaintEvent e) {

					if (null == boldFont) {
						normalFont = e.gc.getFont();
						FontData[] fData = e.gc.getFont().getFontData();
						for (int i = 0; i < fData.length; i++) {
							fData[i].setStyle(SWT.BOLD);
						}
						boldFont = new Font(e.display, fData);

						friendCanvas.addDisposeListener(new DisposeListener() {

							public void widgetDisposed(DisposeEvent e) {
								if (null != boldFont && false == boldFont.isDisposed()) {
									boldFont.dispose();
								}
							}
						});
					}

					/*
					 * Paints the background 
					 */

					Rectangle innerBounds = friendCanvas.getClientArea();

					if (normalColor != null) {
						e.gc.setBackground(normalColor);
					}
					e.gc.fillRectangle(innerBounds);

					/*
					 * Paints the border
					 */
					if (borderColor != null) {
  					e.gc.setForeground(borderColor);
  					e.gc.drawLine(0, innerBounds.height - 1, innerBounds.width,
  							innerBounds.height - 1);
					}

					/*
					 * Paint the text
					 */
//					if (textColor != null) {
//						e.gc.setForeground(textColor);
//					}
					VuzeBuddy vbuddy = FriendWidget.this.buddy;

					Rectangle displayNameBounds = new Rectangle(textAreaBounds.x,
							textAreaBounds.y, textAreaBounds.width, textAreaBounds.height);

					if (null != vbuddy.getDisplayName()
							&& vbuddy.getDisplayName().length() > 0) {
						e.gc.setFont(boldFont);

						Point extent = e.gc.textExtent(vbuddy.getDisplayName() + " ");

						displayNameBounds.width = extent.x;
						GCStringPrinter.printString(e.gc, vbuddy.getDisplayName() + " ",
								displayNameBounds, false, true, SWT.LEFT);

						displayNameBounds.x = extent.x + 6;
						displayNameBounds.width = textAreaBounds.width - extent.x;
						e.gc.setFont(normalFont);

						if (null != vbuddy.getLoginID() && vbuddy.getLoginID().length() > 0) {
							GCStringPrinter.printString(e.gc,
									"(" + vbuddy.getLoginID() + ")", displayNameBounds, false,
									true, SWT.LEFT);
						}
					}

					else {
						if (null != vbuddy.getLoginID() && vbuddy.getLoginID().length() > 0) {
							GCStringPrinter.printString(e.gc, vbuddy.getLoginID(),
									displayNameBounds, false, true, SWT.LEFT);
						}
					}
					/*
					 * Paint the close button
					 */
					if (false == isEmailDisplayOnly()) {
						if (true == closeIsActive) {
							e.gc.drawImage(closeButton_over, closeButtonBounds.x,
									closeButtonBounds.y);
						} else {
							e.gc.drawImage(closeButton, closeButtonBounds.x,
									closeButtonBounds.y);
						}
					}
				}
			});
		}

		public VuzeBuddy getBuddy() {
			return buddy;
		}

		public void setBuddy(VuzeBuddy buddy) {
			this.buddy = buddy;
		}

		public void dispose(boolean animated) {
			if (null != friendCanvas && false == friendCanvas.isDisposed()) {
				if (true == animated) {
					parent.getDisplay().asyncExec(new AERunnable() {

						public void runSupport() {

							/*
							 * KN: TODO: disposal check is still not complete since it could still happen
							 * between the .isDisposed() check and the .redraw() or .update() calls.
							 */
							while (alpha > 20 && false == friendCanvas.isDisposed()) {
								alpha -= 40;
								friendCanvas.redraw();
								friendCanvas.update();

								try {
									Thread.sleep(50);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}

							if (false == friendCanvas.isDisposed()) {
								friendCanvas.dispose();
								parent.layout(true);
							}
						}
					});
				} else {
					if (false == friendCanvas.isDisposed()) {
						friendCanvas.dispose();
						parent.layout(true);
					}
				}

			}
		}

		public Control getControl() {
			return friendCanvas;
		}
	}

	public boolean isEmailDisplayOnly() {
		return isEmailDisplayOnly;
	}

	public void setEmailDisplayOnly(boolean isEmailDisplayOnly) {
		this.isEmailDisplayOnly = isEmailDisplayOnly;
	}

	public BuddiesViewer getBuddiesViewer() {
		return buddiesViewer;
	}

	public void setBuddiesViewer(BuddiesViewer buddiesViewer) {
		this.buddiesViewer = buddiesViewer;
	}

	public int getContentCount() {
		return friendsWidgets.size();
	}

	public List getFriends() {
		List vuzeBuddies = new ArrayList();
		for (Iterator iterator = friendsWidgets.iterator(); iterator.hasNext();) {
			FriendWidget widget = (FriendWidget) iterator.next();
			vuzeBuddies.add(widget.getBuddy());
		}
		return vuzeBuddies;
	}

	public Image getDefault_prompt_image() {
		return default_prompt_image;
	}

	public void setDefault_prompt_image(Image default_prompt_image) {
		this.default_prompt_image = default_prompt_image;
	}

	public String getDefault_prompt_text() {
		return default_prompt_text;
	}

	public void setDefault_prompt_text(String default_prompt_text) {
		this.default_prompt_text = default_prompt_text;
	}
}
