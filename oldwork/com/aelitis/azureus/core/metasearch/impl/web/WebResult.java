/*
 * Created on May 6, 2008
 * Created by Paul Gardner
 * 
 * Copyright 2008 Vuze, Inc.  All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License only.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package com.aelitis.azureus.core.metasearch.impl.web;

import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.lang.*;

import com.aelitis.azureus.core.metasearch.Engine;
import com.aelitis.azureus.core.metasearch.Result;
import com.aelitis.azureus.core.metasearch.impl.DateParser;

public class WebResult extends Result {
	
	
	private static final String HTML_TAGS = "(\\<(/?[^\\>]+)\\>)" ;
	private static final String DUPLICATE_SPACES = "\\s{2,}";
	
	
	String searchQuery;
	
	String rootPageURL;
	String basePageURL;
	DateParser dateParser;
	
	
	String contentType = "";
	String name;
	String category = "";
	
	String drmKey = null;
	
	Date publishedDate;
	
	long size = -1;
	int nbPeers = -1;
	int nbSeeds = -1;
	int nbSuperSeeds = -1;
	int	comments	= -1;
	int votes = -1;
	int votesDown = -1;
	
	boolean privateTorrent;
	
	String cdpLink;
	String torrentLink;
	String downloadButtonLink;
	String playLink;
	
	
	
	public WebResult(Engine engine, String rootPageURL,String basePageURL,DateParser dateParser,String searchQuery) {
		super( engine );
		this.rootPageURL = rootPageURL;
		this.basePageURL = basePageURL;
		this.dateParser = dateParser;
		this.searchQuery = searchQuery;
	}
	
	private static final String removeHTMLTags(String input) {
		String result = input.replaceAll(HTML_TAGS, " ");
		return result.replaceAll(DUPLICATE_SPACES, " ").trim();
	}
	
	public void setName(String name) {
		if(name != null) {
			this.name = name;
		}
	}
	
	public void setNameFromHTML(String name) {
		if(name != null) {
			name = removeHTMLTags(name);
			this.name = Entities.HTML40.unescape(name);
		}
	}
	
	public void setCommentsFromHTML(String comments) {
		if(comments != null) {
			comments = removeHTMLTags(comments);
			comments = Entities.HTML40.unescape(comments);
			comments = comments.replaceAll(",", "");
			comments = comments.replaceAll(" ", "");
			try{
				this.comments = Integer.parseInt(comments);
			}catch( Throwable e ){
				//e.printStackTrace();
			}
		}
	}
	public void setCategoryFromHTML(String category) {
		if(category != null) {
			category = removeHTMLTags(category);
			this.category = Entities.HTML40.unescape(category).trim();
			/*int separator = this.category.indexOf(">");
			
			if(separator != -1) {
				this.category = this.category.substring(separator+1).trim();
			}*/
		}
	}
	
	public void setNbPeersFromHTML(String nbPeers) {
		if(nbPeers != null) {
			nbPeers = removeHTMLTags(nbPeers);
			String nbPeersS = Entities.HTML40.unescape(nbPeers);
			nbPeersS = nbPeersS.replaceAll(",", "");
			nbPeersS = nbPeersS.replaceAll(" ", "");
			try {
				this.nbPeers = Integer.parseInt(nbPeersS);
			} catch(Throwable e) {
				//this.nbPeers = 0;
				//e.printStackTrace();
			}
		}
	}	
	
	public void setNbSeedsFromHTML(String nbSeeds) {
		if(nbSeeds != null) {
			nbSeeds = removeHTMLTags(nbSeeds);
			String nbSeedsS = Entities.HTML40.unescape(nbSeeds);
			nbSeedsS = nbSeedsS.replaceAll(",", "");
			nbSeedsS = nbSeedsS.replaceAll(" ", "");
			try {
				this.nbSeeds = Integer.parseInt(nbSeedsS);
			} catch(Throwable e) {
				//this.nbSeeds = 0;
				//e.printStackTrace();
			}
		}
	}
	
	public void setNbSuperSeedsFromHTML(String nbSuperSeeds) {
		if(nbSuperSeeds != null) {
			nbSuperSeeds = removeHTMLTags(nbSuperSeeds);
			String nbSuperSeedsS = Entities.HTML40.unescape(nbSuperSeeds);
			nbSuperSeedsS = nbSuperSeedsS.replaceAll(",", "");
			nbSuperSeedsS = nbSuperSeedsS.replaceAll(" ", "");
			try {
				this.nbSuperSeeds = Integer.parseInt(nbSuperSeedsS);
			} catch(Throwable e) {
				//this.nbSeeds = 0;
				//e.printStackTrace();
			}
		}
	}
	
	public void setPublishedDate(Date date) {
		this.publishedDate = date;
	}
	
	public void setPublishedDateFromHTML(String publishedDate) {
		if(publishedDate != null) {
			publishedDate = removeHTMLTags(publishedDate);
			String publishedDateS = Entities.HTML40.unescape(publishedDate).replace((char)160,(char)32);
			this.publishedDate = dateParser.parseDate(publishedDateS);
		}
	}
	

	public void setSizeFromHTML(String size) {
		if(size != null) {
			size = removeHTMLTags(size);
			String sizeS = Entities.HTML40.unescape(size).replace((char)160,(char)32);
			sizeS = sizeS.replaceAll("<[^>]+>", " ");
			//Add a space between the digits and unit if there is none
			sizeS = sizeS.replaceFirst("(\\d)([a-zA-Z])", "$1 $2");
			try {
				StringTokenizer st = new StringTokenizer(sizeS," ");
				double base = Double.parseDouble(st.nextToken());
				String unit = "b";
				try {
					unit = st.nextToken().toLowerCase();
				} catch(Throwable e) {
					//No unit
				}
				long multiplier = 1;
				long KB_UNIT = 1024;
				long KIB_UNIT = 1024;
				if("mb".equals(unit)) {
					multiplier = KB_UNIT*KB_UNIT;
				} else if("mib".equals(unit)) {
					multiplier = KIB_UNIT*KIB_UNIT;
				} else if("gb".equals(unit)) {
					multiplier = KB_UNIT*KB_UNIT*KB_UNIT;
				} else if("gib".equals(unit)) {
					multiplier = KIB_UNIT*KIB_UNIT*KIB_UNIT;
				} else if("kb".equals(unit)) {
					multiplier = KB_UNIT;
				} else if("kib".equals(unit)) {
					multiplier = KIB_UNIT;
				}
				
				this.size = (long) (base * multiplier);
			} catch(Throwable e) {
				//e.printStackTrace();
			}
		}
	}
	
	public void setVotesFromHTML(String votes_str) {
		if(votes_str != null) {
			votes_str = removeHTMLTags(votes_str);
			votes_str = Entities.HTML40.unescape(votes_str);
			votes_str = votes_str.replaceAll(",", "");
			votes_str = votes_str.replaceAll(" ", "");
			try {
				this.votes = Integer.parseInt(votes_str);
			} catch(Throwable e) {
				//e.printStackTrace();
			}
		}
	}	
	
	public void setVotesDownFromHTML(String votes_str) {
		if(votes_str != null) {
			votes_str = removeHTMLTags(votes_str);
			votes_str = Entities.HTML40.unescape(votes_str);
			votes_str = votes_str.replaceAll(",", "");
			votes_str = votes_str.replaceAll(" ", "");
			try {
				this.votesDown = Integer.parseInt(votes_str);
			} catch(Throwable e) {
				//e.printStackTrace();
			}
		}
	}
	
	public void setPrivateFromHTML(String privateTorrent) {
		if(privateTorrent != null && ! "".equals(privateTorrent)) {
			this.privateTorrent = true;
		}
	}
	
	public int
	getVotes()
	{
		return( votes );
	}
	
	public int
	getVotesDown()
	{
		return( votesDown );
	}
	
	public void setCDPLink(String cdpLink) {
		this.cdpLink = cdpLink;
	}
	
	public void setDownloadButtonLink(String downloadButtonLink) {
		this.downloadButtonLink = downloadButtonLink;
	}
	
	public void setTorrentLink(String torrentLink) {
		this.torrentLink = torrentLink;
	}
	
	public void setPlayLink(String playLink) {
		this.playLink = playLink;
	}
	
	public String getContentType() {
		return this.contentType;
	}
	
	public String getPlayLink() {
		if(playLink != null) {
			
			if(playLink.startsWith("http://") || playLink.startsWith("https://")) {
				return playLink;
			}
			
			if(playLink.startsWith("/")) {
				return rootPageURL + playLink;
			}
			
			return basePageURL + playLink;
		}
		
		return "";
	}
	
	public void setCategory(String category) {
		this.category = category;
		
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
		
	}
	
	public void setDrmKey(String drmKey) {
		this.drmKey = drmKey;
	}
	

	public String getCDPLink() {
		
		return reConstructLink(cdpLink);
	}

	public String getCategory() {
		return category;
	}

	public String getDownloadLink() {

		return reConstructLink(torrentLink);
		
	}
	
	public String getDownloadButtonLink() {

		//If we don't have a download button link, but we do have a direct download link,
		//then we should use the direct download link...
		if(downloadButtonLink != null) {
			return reConstructLink(downloadButtonLink);
		} else {
			return getDownloadLink();
		}
		
	}
	
	private String reConstructLink(String link) {
		if(link != null) {
			
			if(link.startsWith("http://") || link.startsWith("https://")) {
				return link;
			}
			
			if(link.startsWith("/")) {
				return rootPageURL + link;
			}
			
			return basePageURL + link;
		}
		
		return "";
	}

	public String getName() {
		return name;
	}

	public int getNbPeers() {
		return nbPeers;
	}

	public int getNbSeeds() {
		return nbSeeds;
	}
	
	public int getNbSuperSeeds() {
		return nbSuperSeeds;
	}

	public Date getPublishedDate() {
		return publishedDate;
	}

	public long getSize() {
		return size;
	}
	
	public int
	getComments()
	{
		return( comments );
	}
	
	public String getSearchQuery() {
		return searchQuery;
	}
	
	public boolean isPrivate() {
		return privateTorrent;
	}
	
	public String getDRMKey() {
		return drmKey;
	}
	
	public float getAccuracy() {
		return -1;
	}
}
