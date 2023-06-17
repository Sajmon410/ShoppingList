package simon.radosavljevic.shoppinglist;

public class ZadatakItem {
    private String zadatak;
    private String id;
    private Boolean cekiran;
    public ZadatakItem(String zadatak,Boolean cekiran,String id){
        this.zadatak=zadatak;
        this.cekiran=cekiran;
        this.id=id;
    }
    public String getZadatak(){
        return zadatak;
    }
    public Boolean getCekiran(){
        return cekiran;
    }
    public String getId(){return id;}
    public void setZadatak(String zadatak){
        this.zadatak=zadatak;
    }
    public void setCekiran(Boolean cekiran){
        this.cekiran=cekiran;
    }
}

