package modelo;

public class Curso {
    private String codigo;
    private String idioma;
    private String nivel;
    private String turno;
    private int cupo;
    private double valorCuota;

    public Curso() {}


    public Curso(String codigo, String idioma, String nivel, String turno, int cupo, double valorCuota) {
        this.codigo = codigo;
        this.idioma = idioma;
        this.nivel = nivel;
        this.turno = turno;
        this.cupo = cupo;
        this.valorCuota = valorCuota;
    }

    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getIdioma() {
        return idioma;
    }
    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getNivel() {
        return nivel;
    }


