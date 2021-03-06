/*
 * Copyright 2014 A.C.R. Development
 */
package com.cliqz.browser.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;

import com.cliqz.browser.R;
import com.cliqz.browser.main.Messages;
import com.cliqz.browser.utils.HistoryCleaner;

import acr.browser.lightning.utils.Utils;
import acr.browser.lightning.utils.WebUtils;

public class PrivacySettingsFragment extends BaseSettingsFragment {

    private static final String SETTINGS_LOCATION = "location";
    private static final String SETTINGS_ENABLECOOKIES = "allow_cookies";
    private static final String SETTINGS_THIRDPCOOKIES = "third_party";
    private static final String SETTINGS_SAVEPASSWORD = "password";
    private static final String SETTINGS_CLEARHISTORY = "clear_history";
    // private static final String SETTINGS_COOKIESINKOGNITO = "incognito_cookies";
    private static final String SETTINGS_CACHEEXIT = "clear_cache_exit";
    private static final String SETTINGS_HISTORYEXIT = "clear_history_exit";
    private static final String SETTINGS_COOKIEEXIT = "clear_cookies_exit";
    private static final String SETTINGS_RESTORETOPSITES = "restore_top_sites";
    // private static final String SETTINGS_CLEARCACHE = "clear_cache";
    // private static final String SETTINGS_CLEARCOOKIES = "clear_cookies";
    // private static final String SETTINGS_CLEARWEBSTORAGE = "clear_webstorage";
    // private static final String SETTINGS_WEBSTORAGEEXIT = "clear_webstorage_exit";

    private static final int API = Build.VERSION.SDK_INT;
    private Activity mActivity;
    private CheckBoxPreference cblocation, cbenablecookies, cb3cookies, cbsavepasswords, cbcacheexit,
            cbhistoryexit, cbcookiesexit; //cbcookiesInkognito, cbwebstorageexit
    // private boolean mSystemBrowser;
    private Handler messageHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_privacy);

        mActivity = getActivity();

        initPrefs();
    }

    private void initPrefs() {
        // mPreferenceManager storage
        // mSystemBrowser = mPreferenceManager.getSystemBrowserPresent();

        // !!! Commented out to permit us to restore later !!!!
        // Preference clearcache = findPreference(SETTINGS_CLEARCACHE);
        // Preference clearhistory = findPreference(SETTINGS_CLEARHISTORY);
        // Preference clearcookies = findPreference(SETTINGS_CLEARCOOKIES);
        // Preference clearwebstorage = findPreference(SETTINGS_CLEARWEBSTORAGE);

        cblocation = (CheckBoxPreference) findPreference(SETTINGS_LOCATION);
        cbenablecookies = (CheckBoxPreference) findPreference(SETTINGS_ENABLECOOKIES);
        // cbcookiesInkognito = (CheckBoxPreference) findPreference(SETTINGS_COOKIESINKOGNITO);
        cb3cookies = (CheckBoxPreference) findPreference(SETTINGS_THIRDPCOOKIES);
        cbsavepasswords = (CheckBoxPreference) findPreference(SETTINGS_SAVEPASSWORD);
        cbcacheexit = (CheckBoxPreference) findPreference(SETTINGS_CACHEEXIT);
        cbhistoryexit = (CheckBoxPreference) findPreference(SETTINGS_HISTORYEXIT);
        cbcookiesexit = (CheckBoxPreference) findPreference(SETTINGS_COOKIEEXIT);
        // bwebstorageexit = (CheckBoxPreference) findPreference(SETTINGS_WEBSTORAGEEXIT);

        // clearcache.setOnPreferenceClickListener(this);
        // clearhistory.setOnPreferenceClickListener(this);
        // clearcookies.setOnPreferenceClickListener(this);
        // clearwebstorage.setOnPreferenceClickListener(this);

        cblocation.setOnPreferenceChangeListener(this);
        cbenablecookies.setOnPreferenceChangeListener(this);
        // cbcookiesInkognito.setOnPreferenceChangeListener(this);
        cbsavepasswords.setOnPreferenceChangeListener(this);
        cbcacheexit.setOnPreferenceChangeListener(this);
        cbhistoryexit.setOnPreferenceChangeListener(this);
        cbcookiesexit.setOnPreferenceChangeListener(this);
        // cbwebstorageexit.setOnPreferenceChangeListener(this);

        cblocation.setChecked(mPreferenceManager.getLocationEnabled());
        cbenablecookies.setChecked(mPreferenceManager.getCookiesEnabled());
        // cbcookiesInkognito.setChecked(mPreferenceManager.getIncognitoCookiesEnabled());
        cbsavepasswords.setChecked(mPreferenceManager.getSavePasswordsEnabled());
        cbcacheexit.setChecked(mPreferenceManager.getClearCacheExit());
        cbhistoryexit.setChecked(mPreferenceManager.getClearHistoryExitEnabled());
        cbcookiesexit.setChecked(mPreferenceManager.getClearCookiesExitEnabled());
        // cbwebstorageexit.setChecked(mPreferenceManager.getClearWebStorageExitEnabled());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cb3cookies.setOnPreferenceChangeListener(this);
            cb3cookies.setChecked(mPreferenceManager.getBlockThirdPartyCookiesEnabled());
        } else {
            getPreferenceScreen().removePreference(cb3cookies);
        }

        final Preference prefClearHistory = (Preference) findPreference(SETTINGS_CLEARHISTORY);
        prefClearHistory.setOnPreferenceClickListener(this);
        final Preference restoreTopSites = findPreference(SETTINGS_RESTORETOPSITES);
        restoreTopSites.setOnPreferenceClickListener(this);

        messageHandler = new MessageHandler(mActivity);
    }

    private static class MessageHandler extends Handler {

        final Activity mHandlerContext;

        public MessageHandler(Activity context) {
            this.mHandlerContext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Utils.showSnackbar(mHandlerContext, R.string.message_clear_history);
                    break;
                case 2:
                    Utils.showSnackbar(mHandlerContext, R.string.message_cookies_cleared);
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
//            case SETTINGS_CLEARCACHE:
//                clearCache();
//                return true;
            case SETTINGS_CLEARHISTORY:
                clearHistoryDialog();
                return true;
//            case SETTINGS_CLEARCOOKIES:
//                clearCookiesDialog();
//                return true;
//            case SETTINGS_CLEARWEBSTORAGE:
//                clearWebStorage();
//                return true;
            case SETTINGS_RESTORETOPSITES:
                restoreTopSitesDialog();
                return true;
            default:
                return false;
        }
    }

    private void clearHistoryDialog() {
        final View view = View.inflate(getActivity(), R.layout.dialog_clear_history, null);
        final CheckBox deleteFavoritesCheckbox = (CheckBox) view.findViewById(R.id.clear_history_favorites);
        final CheckBox deleteQueriesCheckbox = (CheckBox) view.findViewById(R.id.clear_history_queries);
        final DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        HistoryCleaner
                                .builder()
                                .setContext(getActivity())
                                .setDeleteFavorites(deleteFavoritesCheckbox.isChecked())
                                .setDeleteQueries(deleteQueriesCheckbox.isChecked())
                                .build()
                                .cleanup();
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(getResources().getString(R.string.title_clear_history))
                    .setMessage(R.string.dialog_history)
                    .setView(view)
                    .setPositiveButton(R.string.action_delete, dialogListener)
                    .setNegativeButton(R.string.action_cancel, dialogListener)
                    .show();
    }

    private void restoreTopSitesDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(getResources().getString(R.string.restore_top_sites));
        builder.setMessage(getResources().getString(R.string.message_restore_top_sites))
                .setPositiveButton(getResources().getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPreferenceManager.setRestoreTopSites(true);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    private void clearCookiesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(getResources().getString(R.string.title_clear_cookies));
        builder.setMessage(getResources().getString(R.string.dialog_cookies))
                .setPositiveButton(getResources().getString(R.string.action_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Thread clear = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        clearCookies();
                                    }
                                });
                                clear.start();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.action_no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        }).show();
    }

    private void clearCache() {
        WebView webView = new WebView(mActivity);
        webView.clearCache(true);
        webView.destroy();
        Utils.showSnackbar(mActivity, R.string.message_cache_cleared);
    }

    private void clearHistory() {
        mHistoryDatabase.clearHistory(false);
        // WebUtils.clearHistory(getActivity());
        messageHandler.sendEmptyMessage(1);
    }

    private void clearCookies() {
        WebUtils.clearCookies(getActivity());
        messageHandler.sendEmptyMessage(2);
    }

    private void clearWebStorage() {
        WebUtils.clearWebStorage();
        Utils.showSnackbar(getActivity(), R.string.message_web_storage_cleared);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // switch preferences
        switch (preference.getKey()) {
            case SETTINGS_LOCATION:
                mPreferenceManager.setLocationEnabled((Boolean) newValue);
                cblocation.setChecked((Boolean) newValue);
                return true;
            case SETTINGS_ENABLECOOKIES:
                mPreferenceManager.setCookiesEnabled((Boolean) newValue);
                cbenablecookies.setChecked((Boolean) newValue);
                return true;
//            case SETTINGS_COOKIESINKOGNITO:
//                mPreferenceManager.setIncognitoCookiesEnabled((Boolean) newValue);
//                cbcookiesInkognito.setChecked((Boolean) newValue);
//                return true;
            case SETTINGS_THIRDPCOOKIES:
                mPreferenceManager.setBlockThirdPartyCookiesEnabled((Boolean) newValue);
                cb3cookies.setChecked((Boolean) newValue);
                return true;
            case SETTINGS_SAVEPASSWORD:
                mPreferenceManager.setSavePasswordsEnabled((Boolean) newValue);
                cbsavepasswords.setChecked((Boolean) newValue);
                return true;
            case SETTINGS_CACHEEXIT:
                mPreferenceManager.setClearCacheExit((Boolean) newValue);
                cbcacheexit.setChecked((Boolean) newValue);
                return true;
            case SETTINGS_HISTORYEXIT:
                mPreferenceManager.setClearHistoryExitEnabled((Boolean) newValue);
                cbhistoryexit.setChecked((Boolean) newValue);
                return true;
            case SETTINGS_COOKIEEXIT:
                mPreferenceManager.setClearCookiesExitEnabled((Boolean) newValue);
                cbcookiesexit.setChecked((Boolean) newValue);
                return true;
//            case SETTINGS_WEBSTORAGEEXIT:
//                mPreferenceManager.setClearWebStorageExitEnabled((Boolean) newValue);
//                cbwebstorageexit.setChecked((Boolean) newValue);
//                return true;
            default:
                return false;
        }
    }
}
