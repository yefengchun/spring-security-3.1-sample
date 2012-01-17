package holiday.security;

import javax.persistence.Transient;

public aspect SecureEntityAspect {
 
	@Transient
	private transient boolean SecureEntity.deletable = false;

	public boolean SecureEntity.isDeletable() {
		return deletable;
	}

	public void SecureEntity.setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

}
