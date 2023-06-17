package simon.radosavljevic.shoppinglist;

public class Zadatak {
    private String naziv;
    private String naziv_liste;
    private String otkacena;
    private String ID;

    public Zadatak(String naziv,String naziv_liste,String otkacena,String ID){
        this.naziv=naziv;
        this.naziv_liste=naziv_liste;
        this.otkacena=otkacena;
        this.ID=ID;
    }
    String getNaziv(){
        return naziv;
    }
    String getNaziv_liste(){
        return naziv_liste;
    }
    String getOtkacena(){
        return otkacena;
    }
    String getID(){
        return ID;
    }
}
