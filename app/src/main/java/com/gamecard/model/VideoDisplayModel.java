package com.gamecard.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by bridgeit on 29/8/16.
 */

public class VideoDisplayModel {

        @SerializedName("vedioLink")
        @Expose
        private String vedioLink;

        @SerializedName("packageName")
        @Expose
        private String packageName;

        @SerializedName("gameTitle")
        @Expose
        private String gameTitle;

        @SerializedName("iconLink")
        @Expose
        private String iconLink;

        @SerializedName("apkLink")
        @Expose
        private Object apkLink;

        /**
         * @return The vedioLink
         */
        public String getVedioLink() {
            return vedioLink;
        }

        /**
         * @param vedioLink The vedioLink
         */
        public void setVedioLink(String vedioLink) {
            this.vedioLink = vedioLink;
        }

        /**
         * @return The packageName
         */
        public String getPackageName() {
            return packageName;
        }

        /**
         * @param packageName The packageName
         */
        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        /**
         * @return The gameTitle
         */
        public String getGameTitle() {
            return gameTitle;
        }

        /**
         * @param gameTitle The gameTitle
         */
        public void setGameTitle(String gameTitle) {
            this.gameTitle = gameTitle;
        }

        /**
         * @return The iconLink
         */
        public String getIconLink() {
            return iconLink;
        }

        /**
         * @param iconLink The iconLink
         */
        public void setIconLink(String iconLink) {
            this.iconLink = iconLink;
        }

        /**
         * @return The apkLink
         */
        public Object getApkLink() {
            return apkLink;
        }

        /**
         * @param apkLink The apkLink
         */
        public void setApkLink(Object apkLink) {
            this.apkLink = apkLink;
        }

    }
