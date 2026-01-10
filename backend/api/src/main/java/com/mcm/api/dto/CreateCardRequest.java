package com.mcm.api.dto;

public class CreateCardRequest {
  private String name;
  private String setName;
  private String rarity;
  private Integer releaseYear;
  private String imageUrl;

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getSetName() { return setName; }
  public void setSetName(String setName) { this.setName = setName; }

  public String getRarity() { return rarity; }
  public void setRarity(String rarity) { this.rarity = rarity; }

  public Integer getReleaseYear() { return releaseYear; }
  public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }

  public String getImageUrl() { return imageUrl; }
  public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
