package model;

public class Satker {
    private Long id;
    private String kodeSatker;
    private String namaSatker;

    public Satker() {
    }

    public Satker(String kodeSatker, String namaSatker) {
        this.kodeSatker = kodeSatker;
        this.namaSatker = namaSatker;
    }

    public Satker(Long id, String kodeSatker, String namaSatker) {
        this.id = id;
        this.kodeSatker = kodeSatker;
        this.namaSatker = namaSatker;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKodeSatker() {
        return kodeSatker;
    }

    public void setKodeSatker(String kodeSatker) {
        this.kodeSatker = kodeSatker;
    }

    public String getNamaSatker() {
        return namaSatker;
    }

    public void setNamaSatker(String namaSatker) {
        this.namaSatker = namaSatker;
    }

    @Override
    public String toString() {
        return "Satker{" +
                "ID= " + id +
                ", Kode Satker= '" + kodeSatker + '\'' +
                ", Nama Satker= '" + namaSatker + '\'' +
                '}';
    }
}
