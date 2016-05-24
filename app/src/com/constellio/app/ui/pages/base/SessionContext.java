package com.constellio.app.ui.pages.base;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;
import java.util.Locale;

import com.constellio.app.ui.entities.UserVO;

public interface SessionContext extends Serializable {

	UserVO getCurrentUser();

	void setCurrentUser(UserVO user);

	String getCurrentCollection();

	void setCurrentCollection(String collection);

	Locale getCurrentLocale();

	void setCurrentLocale(Locale locale);

	String getCurrentUserIPAddress();
	
	boolean isForcedSignOut();
	
	void setForcedSignOut(boolean forcedSignOut);
	
	Principal getUserPrincipal();
	
	void addCollectionChangeListener(CollectionChangeListener listener);
	
	List<CollectionChangeListener> getCollectionChangeListeners();
	
	void removeCollectionChangeListener(CollectionChangeListener listener);
	
	interface CollectionChangeListener {
		
		void collectionChanged(String newCollection);
		
	}
	
}
