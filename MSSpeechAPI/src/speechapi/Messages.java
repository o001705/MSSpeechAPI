package speechapi;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "speechapi.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	public static boolean isExists(String key) {
			return RESOURCE_BUNDLE.containsKey(key);
	}

	public static String MatchKey(String value) {
		Enumeration<String> keys = RESOURCE_BUNDLE.getKeys();
		String val = keys.nextElement();
		boolean found = false;
		while ((found == false) && (val != null)) {
			if (RESOURCE_BUNDLE.getString(val).contains(value))
				found = true;
			else
				val = keys.nextElement();
		}
		if (val == null)
			val = "speaker not enrolled";
		return val;
}
}
