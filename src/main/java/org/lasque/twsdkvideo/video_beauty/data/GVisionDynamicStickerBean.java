package org.lasque.twsdkvideo.video_beauty.data;

import java.util.List;

public class GVisionDynamicStickerBean {

    private List<Categories> categories;
    public void setCategories(List<Categories> categories) {
        this.categories = categories;
    }
    public List<Categories> getCategories() {
        return categories;
    }


    public class Stickers {


        private String name;
        private String nameEn;
        private String id;
        private String stickerId;
        private String previewImage;
        private String stickerUrl;
        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }

        public void setPreviewImage(String previewImage) {
            this.previewImage = previewImage;
        }
        public String getPreviewImage() {
            return previewImage;
        }

        public void setStickerUrl(String stickerUrl) {
            this.stickerUrl = stickerUrl;
        }
        public String getStickerUrl() {
            return stickerUrl;
        }

        public String getNameEn() {
            return nameEn;
        }

        public void setNameEn(String nameEn) {
            this.nameEn = nameEn;
        }

        public String getStickerId() {
            return stickerId;
        }

        public void setStickerId(String stickerId) {
            this.stickerId = stickerId;
        }
    }




    public class Categories {

        private String categoryName;
        private String categoryNameEn;
        private List<Stickers> stickers;
        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }
        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryNameEn(String categoryNameEn) {
            this.categoryNameEn = categoryNameEn;
        }
        public String getCategoryNameEn() {
            return categoryNameEn;
        }

        public void setStickers(List<Stickers> stickers) {
            this.stickers = stickers;
        }
        public List<Stickers> getStickers() {
            return stickers;
        }

    }

}
