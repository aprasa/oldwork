/*
 * Created on Jul 2, 2006 1:48:46 PM
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
package com.aelitis.azureus.ui.swt.views.list;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import org.gudy.azureus2.core3.util.AERunnable;
import org.gudy.azureus2.core3.util.AERunnableObject;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.ui.swt.Utils;
import org.gudy.azureus2.ui.swt.components.BufferedTableItem;
import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
import org.gudy.azureus2.ui.swt.views.table.impl.TableCellImpl;

import com.aelitis.azureus.ui.common.table.TableCellCore;

import org.gudy.azureus2.plugins.ui.tables.TableCellVisibilityListener;
import org.gudy.azureus2.plugins.ui.tables.TableColumn;

/**
 * @author TuxPaper
 * @created Jul 2, 2006
 *
 */
public class ListCell
	implements BufferedTableItem
{
	protected static final boolean DEBUG_COLORCELL = false;

	private String sText;

	protected Color colorFG;

	protected Color colorBG;

	protected Rectangle bounds;

	protected final ListRow row;

	private final int alignment;

	private boolean bLastIsShown = false;

	protected TableCellCore cell;

	protected TableColumn column;

	private Image imgIcon;

	protected ListView view;

	private int fontHeight = -1;
	
	private int maxLines = -1;

	protected TableCellSWT parentCell = null;

	private int secretWidth = -1;

	public ListCell(ListRow row, int alignment, Rectangle bounds) {
		this.row = row;
		this.alignment = alignment;
		this.bounds = bounds;
		this.view = row == null ? null : (ListView) row.getView();

		Utils.execSWTThreadWithObject("getCellFontHeight",
				new AERunnableObject() {
					public Object runSupport() {
						Drawable control = view == null ? (Drawable) Display.getDefault() : (Drawable) view.getControl();
						if (control != null) {
							GC gc = new GC(control);
							try {
								fontHeight = gc.textExtent("(/|,jI~`gy").y;
							} finally {
								gc.dispose();
							}
						}
						return null;
					}
				}, 15000);
	}
	
	public void setView(ListView view) {
		this.view = view;
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void doPaint(GC gc) {
		// TODO: Orientation
		if (gc == null || gc.isDisposed()) {
			Debug.out("gc is null or disposed");
			return;
		}

		if (!isShowable()) {
			return;
		}

		if (sText == null) {
			return;
		}

		gc.setForeground(getForeground());
		gc.setBackground(getBackground());

		if (DEBUG_COLORCELL) {
			gc.setBackground(Display.getDefault().getSystemColor(
					(int) (Math.random() * 13) + 3));
		}
		Rectangle bounds = getBounds();
		if (bounds == null) {
			return;
		}
		gc.fillRectangle(bounds);

		if (((TableCellImpl) cell).bDebug) {
			((TableCellImpl) cell).debug("drawText " + bounds);
		}

		Point size = gc.textExtent(sText);

		boolean hasIcon = (imgIcon != null && !imgIcon.isDisposed());
		if (hasIcon) {
			int w = imgIcon.getBounds().width + 2;
			size.x += w;
			gc.drawImage(imgIcon, bounds.x, bounds.y);
			bounds.x += w;
		}

		size.x += ListView.COLUMN_PADDING_WIDTH;

		if (column.isMaxWidthAuto() && column.getMaxWidth() < size.x) {
			column.setMaxWidth(size.x);
		}
		if (column.isMinWidthAuto() && column.getMinWidth() < size.x) {
			column.setMinWidth(size.x);
		}
		if (column.isPreferredWidthAuto() && column.getPreferredWidth() < size.x) {
			column.setPreferredWidth(size.x);
		}
		//gc.drawText(sText, bounds.x, bounds.y);
		GCStringPrinter.printString(gc, sText, bounds, true, true, alignment
				| SWT.WRAP);
	}

	/**
	 * Whether the column is set to be showable
	 * @return
	 */
	private boolean isShowable() {
		boolean b = bounds != null && bounds.height > 0 && column.isVisible();
		//System.out.println(column.getName() + ";" +  b);
		return b;
	}

	public Color getBackground() {
		if (colorBG == null && row != null) {
			return row.getBackground();
		}

		return colorBG;
	}
	
	public void setBackground(Color bg) {
		colorBG = bg;
	}

	public Rectangle getBounds() {
		TableColumnMetrics columnMetrics = view == null ? null : view.getColumnMetrics(column);
		if (columnMetrics == null || bounds == null || parentCell != null ) {
			Rectangle r = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
			if (parentCell != null) {
				Rectangle parentBounds = parentCell.getBounds();
				if (parentBounds != null) {
					r.x += parentBounds.x;
					r.y += parentBounds.y;
				}
				r.x += parentCell.getMarginWidth();
				r.y += parentCell.getMarginHeight();
			}
			return r;
		}

		bounds.x = columnMetrics.x;
		bounds.width = secretWidth >= 0 ? secretWidth : columnMetrics.width;
		if (row != null) {
  		try {
  			int margin = view.getRowMarginHeight();
  			bounds.y = row.getVisibleYOffset() + margin;
  			//bounds.height = view.DEFAULT_ROW_HEIGHT - (ListView.ROW_MARGIN_HEIGHT * 2);
  			bounds.height = row.getHeight() - (margin * 2);
  		} catch (Exception e) {
  			//System.err.println(cell.getTableColumn().getName() + " " + bounds + ";" + row + ";");
  		}
		}
		return bounds;
	}

	/**
	 * @param bounds the bounds to set
	 */
	public void setBounds(Rectangle bounds) {
		if (((TableCellImpl) cell).bDebug) {
			((TableCellImpl) cell).debug("setBounds " + bounds);
		}
		
		//System.out.println(cell.getTableID() + "]" + cell.getTableColumn().getName() + ": " + bounds + " via " + Debug.getCompressedStackTrace());
		this.bounds = bounds;
		//this.ourBounds = true;
	}
	
	public void setSecretWidth(int width) {
		secretWidth  = width;
	}

	public int getPosition() {
		return column.getPosition();
	}

	public String getText() {
		return sText;
	}

	public boolean isShown() {
		boolean bIsShown = isShowable();
		if (row != null) {
			bIsShown &= row.isVisible();
		}
		if (bIsShown != bLastIsShown) {
			bLastIsShown = bIsShown;
			if (cell != null) {
				int mode = bIsShown ? TableCellVisibilityListener.VISIBILITY_SHOWN
						: TableCellVisibilityListener.VISIBILITY_HIDDEN;
				cell.invokeVisibilityListeners(mode, true);
			}
		}
		return bIsShown;
	}

	public void locationChanged() {
		// TODO Auto-generated method stub

	}

	public boolean needsPainting() {
		return isShown();
	}

	public void refresh() {
		redraw();
	}

	public void setIcon(Image img) {
		imgIcon = img;
	}

	public Image getIcon() {
		return imgIcon;
	}

	public boolean setForeground(Color color) {
		if (isSameColor(colorFG, color)) {
			return false;
		}
		colorFG = color;
		return true;
	}

	private boolean isSameColor(Color c1, Color c2) {
		if (c1 == null && c2 == null) {
			return true;
		}
		if (c1 == null || c2 == null) {
			return false;
		}
		return c1.getRGB().equals(c2.getRGB());
	}

	public boolean setForeground(int red, int green, int blue) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setRowForeground(Color color) {
		// TODO Auto-generated method stub

	}

	public boolean setText(String text) {
		if (sText == null && text == null) {
			return false;
		}
		if (sText != null && text != null && (sText == text || sText.equals(text))) {
			return false;
		}

		sText = text;

		if (view != null) {
			view.cellRefresh(this, true, true);
		}

		return true;
	}

	public void redraw() {
		// XXX Complete.  We don't have a redraw for cells, so redraw the row
		if (!isShown()) {
			return;
		}
		if (row != null) {
			row.redraw();
		}
		// invalidating the area
		//	Utils.execSWTThread(new AERunnable() {
		//	public void runSupport() {
		//		if (!isShown()) {
		//			return;
		//		}
		//  	Rectangle r = getBounds();
		//  	if (r == null) {
		//  		return;
		//  	}
		//  	((TableViewSWT) row.getView()).getTableComposite().redraw(r.x, r.y,
		//				r.width, r.height, true);
		//	}
		//});
	}

	public void invalidate() {
	}

	// @see org.gudy.azureus2.ui.swt.components.BufferedTableItem#getBackgroundImage()
	public Image getBackgroundImage() {
		Rectangle bounds = getBounds();
		
		if (bounds == null || bounds.isEmpty()) {
			return null;
		}
		
		Image image = new Image(Display.getDefault(), bounds.width, bounds.height);
		
		GC gc = new GC(image);
		gc.setBackground(getBackground());
		gc.setForeground(getBackground());
		gc.fillRectangle(image.getBounds());
		gc.dispose();
		
		//GC gc = new GC(composite);
		//gc.copyArea(image, bounds.x, bounds.y);
		//gc.dispose();
		
		return image;
	}

	public Color getForeground() {
		if (colorFG == null && row != null) {
			return row.getForeground();
		}

		return colorFG;
	}

	public ListRow getRow() {
		return row;
	}

	/**
	 * @param cell
	 */
	public void setTableCell(TableCellCore cell) {
		this.cell = cell;
		this.column = cell.getTableColumn();
	}

	// @see org.gudy.azureus2.ui.swt.components.BufferedTableItem#getMaxLines()
	public int getMaxLines() {
		if (maxLines <= 0) {
  		if (fontHeight <= 0) {
  			maxLines = 1;
  		} else {
  			Rectangle bounds = getBounds();
  			if (bounds == null) {
  				return 1;
  			} else { 
  				maxLines = (int) Math.ceil((double)bounds.height / fontHeight);
  			}
  		}
		}
		return maxLines;
	}

	// @see org.gudy.azureus2.ui.swt.components.BufferedTableItem#setCursor(int)
	public void setCursor(final int cursorID) {
		if (view == null) {
			return;
		}
		Utils.execSWTThread(new AERunnable() {
			public void runSupport() {
				Composite composite = view.getComposite();
				composite.setCursor(composite.getDisplay().getSystemCursor(cursorID));
			}
		});
	}
	
	// @see org.gudy.azureus2.ui.swt.components.BufferedTableItem#isMouseOver()
	public boolean isMouseOver() {
		return getMouseRelative() != null;
	}
	
	public Point getMouseRelative() {
		if (view == null || view.isDisposed()) {
			return null;
		}

		Rectangle bounds = getBounds();

		if (parentCell != null &&  false) {
			Point parentMouseRel = ((ListCell)parentCell.getBufferedTableItem()).getMouseRelative();
			if (parentMouseRel == null) {
				return null;
			}
			Point rel = new Point(parentMouseRel.x - bounds.x, parentMouseRel.y - bounds.y);

			if (rel.x < 0 || rel.y < 0 || rel.x >= bounds.width || rel.y >= bounds.height) {
				return null;
			}

			return rel;
		} else {
			Composite table = view.getTableComposite();
			Point pt = Display.getCurrent().getCursorLocation();
			pt = table.toControl(pt);

			if (!bounds.contains(pt)) {
				return null;
			}

			Point rel = new Point(pt.x - bounds.x, pt.y - bounds.y);
			return rel;
		}

	}

	public TableCellSWT getParentCell() {
		return parentCell;
	}

	public void setParentCell(TableCellSWT parentCell) {
		this.parentCell = parentCell;
	}
}
