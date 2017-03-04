package com.smartmarmot.dbforbix.db.adapter;

import java.util.Set;

import com.smartmarmot.dbforbix.db.DBType;


public class SYBASE extends AbstractAdapter {

	public SYBASE(String name, String url, String user, String passwd,Integer maxactive, Integer maxidle,Integer maxwaitmillis, Set<String> set,Boolean pers) {
		this.name = name;
		this.url = url;
		this.user = user;
		this.passwd = passwd;
		this.maxactive = maxactive.intValue();
		this.maxidle = maxidle.intValue();
		this.maxwaitmillis=maxwaitmillis.intValue();
		this.itemGroupNames=set;
		this.persistence = pers;
	}
	
	@Override
	public DBType getType() {
		return DBType.SYBASE;
	}

}
