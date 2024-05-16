package org.webscraper.model;

public class Payload {
    private String latlng;
    private String keyword;
    private int offset;
    private int pageSize;
    private String countryCode;

    // Default constructor
    public Payload() {
    }

    // Parameterized constructor
    public Payload(String latlng, String keyword, int offset, int pageSize, String countryCode) {
        this.latlng = latlng;
        this.keyword = keyword;
        this.offset = offset;
        this.pageSize = pageSize;
        this.countryCode = countryCode;
    }

    // Getter and setter methods
    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}