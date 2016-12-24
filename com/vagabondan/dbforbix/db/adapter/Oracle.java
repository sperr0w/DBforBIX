/*
 * This file is part of DBforBix.
 *
 * DBforBix is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * DBforBix is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * DBforBix. If not, see <http://www.gnu.org/licenses/>.
 */

package com.vagabondan.dbforbix.db.adapter;

import java.util.Set;

import com.vagabondan.dbforbix.db.DBType;


public class Oracle extends AbstractAdapter {

	public Oracle(String name, String url, String user, String passwd, Integer maxactive,Integer maxidle,Integer maxwaitmillis, Set<String> set, Boolean pers) {
		this.name = name;
		this.url = url;
		this.user = user;
		this.passwd = passwd;
		this.maxactive = maxactive;
		this.maxwaitmillis = maxwaitmillis;
		this.maxidle = maxidle;
		this.itemGroupName = set;
		this.persistence = pers;
	}
	


	@Override
	public DBType getType() {
		return DBType.ORACLE;
	}


}
