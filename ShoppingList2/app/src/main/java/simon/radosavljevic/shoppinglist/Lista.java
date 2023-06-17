package simon.radosavljevic.shoppinglist;

public class Lista {
    private String ListName;
    private String CreatorName;
    private String Shared;

    public Lista(String ListName, String CreatorName, String Shared){
        this.ListName=ListName;
        this.CreatorName=CreatorName;
        this.Shared=Shared;
    }
    public String getListName(){
        return ListName;
    }
    public String getCreatorName(){
        return CreatorName;
    }
    public String getShared(){
        return Shared;
    }
}
