package jroar.web.model;

import org.springframework.stereotype.Component;

@Component
public class InstallerInfo {
	private boolean isInstall;

	public InstallerInfo() {
		isInstall=false;
	}
	
	public boolean isInstall() {
		return isInstall;
	}

	public void setInstall(boolean isInstall) {
		this.isInstall = isInstall;
	}
	
	
}
