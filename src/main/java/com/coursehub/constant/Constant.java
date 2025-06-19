package com.coursehub.constant;

public class Constant {

    private Constant() {
        // Private constructor to prevent instantiation
    }

    public static class CommonConstants {

        private CommonConstants() {
            // Private constructor to prevent instantiation
        }

        public static final String SUCCESS = "com.coursehub.constant.CommonConstans.SUCCESS";

        public static final String COMMENT = "COMMENT";

        public static final String REVIEW = "REVIEW";

        public static final String ADMIN = "ADMIN";


    }

    public static class SearchConstants {

        private SearchConstants() {
            // Private constructor to prevent instantiation
        }

        // Default values
        public static final String DEFAULT_SORT_BY = "createdDate";
        public static final String DEFAULT_SORT_DIRECTION = "desc";
        public static final int DEFAULT_PAGE_SIZE = 10;
        public static final int DEFAULT_PAGE_NUMBER = 0;

        // Sort directions
        public static final String SORT_ASC = "asc";
        public static final String SORT_DESC = "desc";

        // Sortable fields
        public static final String SORT_BY_TITLE = "title";
        public static final String SORT_BY_PRICE = "price";
        public static final String SORT_BY_CREATED_DATE = "createdDate";
        public static final String SORT_BY_RATING = "averageRating";

    }
}