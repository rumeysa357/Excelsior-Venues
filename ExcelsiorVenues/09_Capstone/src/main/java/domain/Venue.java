package domain;

public class Venue {

    private Long id;
    private String name;
    private int cityId;
    private String description;
    private String cityname;
    private  String stateabbreviation;
    private String category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getStateAbbreviation() {
        return stateabbreviation;
    }

    public void setStateAbbreviation(String stateabbreviation) {
        this.stateabbreviation = stateabbreviation;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
