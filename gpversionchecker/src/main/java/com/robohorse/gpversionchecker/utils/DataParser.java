package com.robohorse.gpversionchecker.utils;

import android.text.Html;
import android.text.TextUtils;

import com.robohorse.gpversionchecker.debug.ALog;
import com.robohorse.gpversionchecker.domain.Version;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by vadim on 17.07.16.
 */
public class DataParser {
    private static final String DIV_VERSION = "span[class=htlgb]";
    private static final String DIV_CHANGES = "div[class=DWPxHb]";
    private static final String DIV_DESCRIPTION = "div[itemprop=description]";

    public Version parse(Document document, final String currentVersion, final String url) {
         String newVersion ="";
        if (document != null) {
            Elements element = document.getElementsContainingOwnText("Current Version");
            for (Element ele : element) {
                if (ele.siblingElements() != null) {
                    Elements sibElemets = ele.siblingElements();
                    for (Element sibElemet : sibElemets) {
                        newVersion = sibElemet.text();
                    }
                }
            }
        }

        final String description = String.valueOf(Html.fromHtml(document.select(DIV_DESCRIPTION)
                .html()));

        String changes = null;
        final Elements elements = document.select(DIV_CHANGES);
        if (null != elements) {
            final Elements changesElements = elements.select(DIV_CHANGES);
            if (!changesElements.isEmpty()) {
                changes = String.valueOf(Html.fromHtml(changesElements.last().html()));
                if (TextUtils.equals(changes, description)) {
                    changes = null;
                }
            }
        }

        ALog.d("current version: " + currentVersion + "; google play version: " + newVersion);

        if (TextUtils.isEmpty(newVersion) || TextUtils.isEmpty(currentVersion)) {
            return null;
        }

        final int currentVersionValue = Integer.parseInt(replaceNonDigits(currentVersion));
        final int newVersionValue = Integer.parseInt(replaceNonDigits(newVersion));

        final boolean needToUpdate = newVersionValue > currentVersionValue;

        return new Version.Builder()
                .setNewVersionCode(newVersion)
                .setChanges(changes)
                .setNeedToUpdate(needToUpdate)
                .setUrl(url)
                .setDescription(description)
                .build();
    }

    private String replaceNonDigits(String value) {
        value = value.replaceAll("[^\\d.]", "");
        value = value.replace(".", "");
        return value;
    }
}
