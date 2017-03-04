/*
 * This file is part of DB4bix.
 *
 * DB4bix is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * DB4bix is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * DB4bix. If not, see <http://www.gnu.org/licenses/>.
 */

package com.vagabondan.db4bix.scheduler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.vagabondan.db4bix.DB4bix;
import com.vagabondan.db4bix.config.Config;
import com.vagabondan.db4bix.db.DBManager;
import com.vagabondan.db4bix.db.adapter.Adapter;
import com.vagabondan.db4bix.zabbix.ZabbixItem;
import com.vagabondan.db4bix.zabbix.ZabbixSender;

/**
 * The item fetching class
 * 
 * @author Andrea Dalle Vacche
 */
public class Scheduler extends TimerTask {

	private static final Logger		LOG		= Logger.getLogger(Scheduler.class);
	private boolean					working	= false;
	private int						pause;

	private Map<String, Set<Item>>	globalItems;
	//private Map<String, Set<Item>>	serverItems;

	/**
	 * Creates a new TimeTask for item fetching
	 * 
	 * @param taskGroup
	 *            the schedule group of this worker
	 * @param databaseCfg
	 *            the database config to use
	 */
	public Scheduler(int pause) {
		this.pause = pause;

		globalItems = new ConcurrentHashMap<String, Set<Item>>(9);
		//serverItems = new ConcurrentHashMap<String, Set<Item>>(9);
	}

	public void addItem(String itemGroupName, Item item) {
		if (!globalItems.containsKey(itemGroupName))
			globalItems.put(itemGroupName, new HashSet<Item>());
		globalItems.get(itemGroupName).add(item);
	}

	public int getPause() {
		return pause;
	}

	@Override
	public void run() {
		if (working)
			return;
		working = true;
		DBManager dbman = DBManager.getInstance();
		ZabbixSender sender = DB4bix.getZSender();
		Config config = Config.getInstance();
		try {
			LOG.debug("Scheduler.run() " + getPause());
			// <itemGroupName>:<ConfigItem>
			for (Entry<String, Set<Item>> set : globalItems.entrySet()) {
				// <itemGroupName> -> DBs monitored
				Adapter[] targetDB = dbman.getDatabases(set.getKey());
				if (targetDB != null && targetDB.length > 0) {
					for (Adapter db : targetDB) {
						Connection con = db.getConnection();
						try {
							for (Item item : set.getValue()) {
								try {
									ZabbixItem[] result = item.getItemData(con, config.getQueryTimeout());
									if (result != null)
										for (ZabbixItem i : result)
											sender.addItem(i);
								}
								catch (SQLTimeoutException sqlex) {
									LOG.warn("item timed out after "+config.getQueryTimeout()+"s: " + item.getName(), sqlex);
								}
								catch (SQLException sqlex) {
									LOG.warn("could not fetch value " + item.getName(), sqlex);
								}
							}
						}
						finally {
							con.close();
						}
					}
				}
			}
		}
		catch (Throwable th) {
			LOG.error(th.getMessage(), th);
		}
		working = false;
	}
}
