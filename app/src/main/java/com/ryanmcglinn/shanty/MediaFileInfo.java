package com.ryanmcglinn.shanty;

/**
 * Helper class imported from https://github.com/mukesh4u/MediaListing
 */
public class MediaFileInfo {
    private String fileName;
    private String filePath;
    private String artist;
    private String album;
    private String duration;
    private String id;

    public MediaFileInfo(String pfileName, String pFilePath, String pArtist, String pAlbum, String pDuration, String pId){
        fileName = pfileName;
        filePath = pFilePath;
        artist = pArtist;
        album = pAlbum;
        duration = pDuration;
        id = pId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getDuration() {
        return duration;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString(){
        return fileName;
    }
}
