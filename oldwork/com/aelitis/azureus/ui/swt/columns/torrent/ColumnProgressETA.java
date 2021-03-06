/**
 * 
 */
package com.aelitis.azureus.ui.swt.columns.torrent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;

import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.download.DownloadManagerState;
import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.core3.util.DisplayFormatters;
import org.gudy.azureus2.core3.util.SystemTime;
import org.gudy.azureus2.core3.util.TimeFormatter;
import org.gudy.azureus2.ui.swt.ImageRepository;
import org.gudy.azureus2.ui.swt.Utils;
import org.gudy.azureus2.ui.swt.mainwindow.Colors;
import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
import org.gudy.azureus2.ui.swt.views.table.TableCellSWTPaintListener;
import org.gudy.azureus2.ui.swt.views.table.utils.CoreTableColumn;

import com.aelitis.azureus.core.download.DownloadManagerEnhancer;
import com.aelitis.azureus.core.download.EnhancedDownloadManager;
import com.aelitis.azureus.ui.common.table.TableRowCore;
import com.aelitis.azureus.ui.swt.skin.SWTSkinFactory;
import com.aelitis.azureus.ui.swt.skin.SWTSkinProperties;

import org.gudy.azureus2.plugins.ui.tables.TableCell;
import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;

/**
 * @author TuxPaper
 * @created Jun 13, 2006
 *
 */
public class ColumnProgressETA
	extends CoreTableColumn
	implements TableCellAddedListener
{
	public static final String COLUMN_ID = "ProgressETA";

	private static final int borderWidth = 1;

	private static final int COLUMN_WIDTH = 150;

	public static final long SHOW_ETA_AFTER_MS = 30000;

	private static Font fontText = null;

	Display display;

	private Color cBG;

	private Color cFG;

	private Color cBorder;

	private Color cText;
	
	static {
		ImageRepository.addPath("org/gudy/azureus2/ui/icons/dl_bar_end.png", "dl_bar_end");
		ImageRepository.addPath("org/gudy/azureus2/ui/icons/dl_bar_0.png", "dl_bar_0");
		ImageRepository.addPath("org/gudy/azureus2/ui/icons/dl_bar_1.png", "dl_bar_1");
	}
	
	Image imgEnd;
	Image img1;
	Image img0;
	
	Color textColor;

	/**
	 * 
	 */
	public ColumnProgressETA(String sTableID) {
		super(COLUMN_ID, sTableID);
		initializeAsGraphic(COLUMN_WIDTH);
		setAlignment(ALIGN_LEAD);
		setMinWidth(COLUMN_WIDTH);

		display = SWTThread.getInstance().getDisplay();
		
		SWTSkinProperties skinProperties = SWTSkinFactory.getInstance().getSkinProperties();
		cBG = skinProperties.getColor("color.progress.bg");
		if (cBG == null) {
			cBG = Colors.blues[Colors.BLUES_DARKEST];
		}
		cFG = skinProperties.getColor("color.progress.fg");
		if (cFG == null) {
			cFG = Colors.blues[Colors.BLUES_LIGHTEST];
		}
		cBorder = skinProperties.getColor("color.progress.border");
		if (cBorder == null) {
			cBorder = Colors.grey;
		}
		cText = skinProperties.getColor("color.progress.text");
		if (cText == null) {
			cText = Colors.black;
		}
		
		imgEnd = ImageRepository.getImage("dl_bar_end");
		img0 = ImageRepository.getImage("dl_bar_0");
		img1 = ImageRepository.getImage("dl_bar_1");

	}

	public void cellAdded(TableCell cell) {
		new Cell(cell);
	}
	
	private class Cell
		implements TableCellRefreshListener,
		TableCellSWTPaintListener
	{
		int lastPercentDone = 0;

		long lastETA;

		public Cell(TableCell cell) {
			cell.addListeners(this);
			cell.setMarginHeight(3);
			//cell.setFillCell(true);
		}

		public void refresh(TableCell cell) {
			DownloadManager dm = (DownloadManager) cell.getDataSource();
			if (dm == null) {
				return;
			}

			int percentDone = getPercentDone(cell);

			long sortValue = 0;

			long completedTime = dm.getDownloadState().getLongParameter(
					DownloadManagerState.PARAM_DOWNLOAD_COMPLETED_TIME);
			if (completedTime <= 0) {
				sortValue = dm.getDownloadState().getLongParameter(
						DownloadManagerState.PARAM_DOWNLOAD_ADDED_TIME) * 10000;
				sortValue += 1000 - percentDone;
			} else {
				sortValue = completedTime;
			}

			long eta = getETA(cell);
			
			if (!cell.setSortValue(sortValue) && cell.isValid()
					&& lastPercentDone == percentDone && lastETA == eta) {
				return;
			}

			lastPercentDone = percentDone;
			lastETA = eta;

			cell.setSortValue(sortValue);
			cell.invalidate();
		}
		
		// @see org.gudy.azureus2.ui.swt.views.table.TableCellSWTPaintListener#cellPaint(org.eclipse.swt.graphics.GC, org.gudy.azureus2.ui.swt.views.table.TableCellSWT)
		public void cellPaint(GC gcImage, TableCellSWT cell) {
			DownloadManager dm = (DownloadManager) cell.getDataSource();
			if (dm == null) {
				return;
			}
			int percentDone = getPercentDone(cell);
			long eta = getETA(cell);
		
			//Compute bounds ...
			int newWidth = cell.getWidth();
			if (newWidth <= 0) {
				return;
			}
			int newHeight = cell.getHeight();
			
			Color fgFirst = gcImage.getForeground();
			
			Rectangle cellBounds = cell.getBounds();
			
			int x0 = cellBounds.x + cell.getMarginWidth();
			int y0 = cellBounds.y + cell.getMarginHeight();

			int x1 = borderWidth;
			int y1 = borderWidth;
			int x2 = newWidth - x1 - borderWidth;
			int progressX2 = x2;
			int progressY2 = newHeight - y1 - borderWidth - 13;
			if (progressY2 > 18) {
				progressY2 = 18;
			}
			boolean showSecondLine = progressY2 > 0;
			if (!showSecondLine) {
				progressY2 = newHeight;
			}
			
			if (x2 < 10 || progressX2 < 10) {
				return;
			}

			boolean bDrawProgressBar = true;

			String sETALine = null;
			long lSpeed = getSpeed(dm);
			String sSpeed = lSpeed <= 0 ? "" : "("
					+ DisplayFormatters.formatByteCountToKiBEtcPerSec(lSpeed, true) + ")";

			if (bDrawProgressBar && percentDone < 1000) {
				
				gcImage.drawImage(imgEnd, x0, y0+y1);
				gcImage.drawImage(imgEnd, x0 + progressX2 - x1 + 1, y0+y1);
				
				
				
//				gcImage.setForeground(cBorder);
//				gcImage.drawRectangle(x0, y0, progressX2 - x1 + 1, progressY2 - y1 + 1);

				int limit = ((progressX2 - x1) * percentDone) / 1000;

				gcImage.drawImage(img1, 0, 0, 1, 13,x0 + x1, y0 + y1, limit, 13);
				
				
//				gcImage.setBackground(cBG);
//				gcImage.fillRectangle(x0 + x1, y0 + y1, limit, progressY2 - y1);
				if (limit < progressX2) {
					gcImage.drawImage(img0, 0, 0, 1, 13, x0 + limit + 1, y0 + y1, progressX2 - limit - 1,13);
//					gcImage.setBackground(cFG);
//					gcImage.fillRectangle(x0 + limit + 1, y0 + y1, progressX2 - limit - 1,
//							progressY2 - y1);
				}

			}

			if (sETALine == null) {
				//if (isStopped(cell)) {
				//sETALine = DisplayFormatters.formatDownloadStatus((DownloadManager) cell.getDataSource());
				//} else
				if (dm.isDownloadComplete(true)) {
					sETALine = DisplayFormatters.formatByteCountToKiBEtc(dm.getSize());
				} else if (eta > 0) {
					String sETA = TimeFormatter.format(eta);
					sETALine = MessageText.getString(
							"MyTorrents.column.ColumnProgressETA.2ndLine", new String[] {
								sETA
							});
				} else {
					sETALine = DisplayFormatters.formatDownloadStatus(dm);
					//sETALine = "";
				}
			}

			if (fontText == null) {
				fontText = Utils.getFontWithHeight(gcImage.getFont(), gcImage, 11);
			}

			gcImage.setTextAntialias(SWT.ON);
			if (showSecondLine) {
  			gcImage.setFont(fontText);
  			//int[] fg = cell.getForeground();
  			//gcImage.setForeground(ColorCache.getColor(display, fg[0], fg[1], fg[2]));
  			gcImage.setForeground(fgFirst);
  			gcImage.drawText(sETALine, x0 + 2, y0 + progressY2, true);
  			Point textExtent = gcImage.textExtent(sETALine);
  			cell.setToolTip(textExtent.x > newWidth ? sETALine : null);
			}
			int middleY = (progressY2 - 12) / 2;
			if (percentDone == 1000) {
				gcImage.setForeground(cText);
				gcImage.drawText("Complete", x0 + 2, y0 + middleY, true);
			} else if (bDrawProgressBar) {
				gcImage.setForeground(cText);
				String sPercent = DisplayFormatters.formatPercentFromThousands(percentDone);
				gcImage.drawText(sSpeed, x0 + 50, y0 + y1 + 1, true);
				gcImage.drawText(sPercent, x0 + 2, y0 + y1 + 1, true);
			}
  
			gcImage.setFont(null);
		}

		private int getPercentDone(TableCell cell) {
			DownloadManager dm = (DownloadManager) cell.getDataSource();
			if (dm == null) {
				return 0;
			}
			return dm.getStats().getDownloadCompleted(true);
		}

		private long getETA(TableCell cell) {
			DownloadManager dm = (DownloadManager) cell.getDataSource();
			if (dm == null) {
				return 0;
			}
			long diff = SystemTime.getCurrentTime() - dm.getStats().getTimeStarted();
			if (diff > SHOW_ETA_AFTER_MS) {
				return dm.getStats().getETA();
			}
			return 0;
		}

		private int getState(TableCell cell) {
			DownloadManager dm = (DownloadManager) cell.getDataSource();
			if (dm == null) {
				return DownloadManager.STATE_ERROR;
			}
			return dm.getState();
		}

		private boolean isStopped(TableCell cell) {
			int state = getState(cell);
			return state == DownloadManager.STATE_QUEUED
					|| state == DownloadManager.STATE_STOPPED
					|| state == DownloadManager.STATE_STOPPING
					|| state == DownloadManager.STATE_ERROR;
		}

		private long getSpeed(DownloadManager dm) {
			if (dm == null) {
				return 0;
			}

			return dm.getStats().getDataReceiveRate();
		}

		public EnhancedDownloadManager getEDM(DownloadManager dm) {
			DownloadManagerEnhancer dmEnhancer = DownloadManagerEnhancer.getSingleton();
			if (dmEnhancer == null) {
				return null;
			}
			return dmEnhancer.getEnhancedDownload(dm);
		}


		private void log(TableCell cell, String s) {
			System.out.println(((TableRowCore) cell.getTableRow()).getIndex() + ":"
					+ System.currentTimeMillis() + ": " + s);
		}
	}
}
