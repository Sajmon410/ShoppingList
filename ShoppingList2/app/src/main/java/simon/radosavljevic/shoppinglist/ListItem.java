package simon.radosavljevic.shoppinglist;

public class ListItem {
    private String naslov;
    private Boolean shared;

    public ListItem(String naslov,Boolean shared)
    {
        this.naslov=naslov;
        this.shared=shared;
    }
    public String getNaslov(){
        return naslov;
    }
    public Boolean getShared(){
        return shared;
    }
    public void setNaslov(String naslov){
        this.naslov=naslov;
    }
    public void setShared(Boolean shared){
        this.shared=shared;
    }
}
