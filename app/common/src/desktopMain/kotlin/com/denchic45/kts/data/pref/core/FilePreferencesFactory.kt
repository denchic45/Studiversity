package com.denchic45.kts.data.pref.core

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings


/**
 * PreferencesFactory implementation that stores the preferences in a user-defined file. To use it,
 * set the system property <tt>java.util.prefs.PreferencesFactory</tt> to
 * <tt>net.infotrek.util.prefs.FilePreferencesFactory</tt>
 *
 *
 * The file defaults to [user.home]/.fileprefs, but may be overridden with the system property
 * <tt>net.infotrek.util.prefs.FilePreferencesFactory.file</tt>
 *
 * @author David Croft ([www.davidc.net](http://www.davidc.net))
 * @version $Id: FilePreferencesFactory.java 282 2009-06-18 17:05:18Z david $
 */

class FilePreferencesFactory : Settings.Factory {
    override fun create(name: String?): Settings {
        return PreferencesSettings(FilePreferences(null, name!!))
    }
}