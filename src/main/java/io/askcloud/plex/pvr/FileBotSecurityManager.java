/**
 * 
 */
package io.askcloud.plex.pvr;

import java.security.Permission;

/**
 * @author git@askcloud.io
 *
 */
public class FileBotSecurityManager extends SecurityManager {

	/**
	 * 
	 */
	public FileBotSecurityManager() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void checkPermission(Permission perm) {
//		// TODO Auto-generated method stub
//		super.checkPermission(perm);
	}
	
	@Override
	public void checkExit(int status) {
		throw new SecurityException();
	}
}
