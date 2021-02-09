package zgas.client.models;

import com.google.android.gms.maps.model.LatLng;

import zgas.client.Home;
import zgas.client.R;

public class Direcciones {

    private String etiqueta;
    private String calle;
    private String colonia;
    private String codigo_postal;
    private String domicilioLargo;

    private String numExterior;
    private String numInterior;
    private String SParticular;

    private int empresa;
    private String ruta;
    private LatLng mLatLng;

    private int slot;
    private int mFlagImage;
    Ruta mRuta;

    public static boolean isLoad() {
        return isLoad;
    }

    public static void setIsLoad(boolean isLoad) {
        Direcciones.isLoad = isLoad;
    }

    public static short getSlotDefault() {
        return slotDefault;
    }

    public static void setSlotDefault(short slotDefault) {
        Direcciones.slotDefault = slotDefault;
    }

    private static boolean isLoad = false; //Llave de carga
    private static short slotDefault=0; //Llave de carga

    public String getEtiqueta() {
        return etiqueta;
    }

    public Direcciones setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
        return this;
    }

    public String getCalle() {
        return calle;
    }

    public Direcciones setCalle(String calle) {
        this.calle = calle;
        return this;
    }

    public String getColonia() {
        return colonia;
    }

    public Direcciones setColonia(String colonia) {
        this.colonia = colonia;
        return this;
    }

    public String getCodigo_postal() {
        return codigo_postal;
    }

    public Direcciones setCodigo_postal(String codigo_postal) {
        this.codigo_postal = codigo_postal;
        return this;
    }

    public String getDomicilioLargo() {
        return domicilioLargo;
    }

    public Direcciones setDomicilioLargo(String domicilioLargo) {
        this.domicilioLargo = domicilioLargo;
        return this;
    }

    public String getNumExterior() {
        return numExterior;
    }

    public Direcciones setNumExterior(String numExterior) {
        this.numExterior = numExterior;
        return this;
    }

    public String getNumInterior() {
        return numInterior;
    }

    public Direcciones setNumInterior(String numInterior) {
        this.numInterior = numInterior;
        return this;
    }

    public String getSParticular() {
        return SParticular;
    }

    public Direcciones setSParticular(String SParticular) {
        this.SParticular = SParticular;
        return this;
    }

    public int getEmpresa() {
        return empresa;
    }

    public Direcciones setEmpresa(int empresa) {
        this.empresa = empresa;
        return this;
    }

    public String getRuta() {
        return ruta;
    }

    public Direcciones setRuta(LatLng latLng) {
        this.ruta = mRuta.getRuta(latLng);
        return this;
    }

    public Direcciones setRuta(String ruta) {
        this.ruta = ruta;
        return this;
    }

    public LatLng getmLatLng() {
        return mLatLng;
    }

    public Direcciones setmLatLng(LatLng mLatLng) {
        this.mLatLng = mLatLng;
        return this;
    }

    public int getSlot() {
        return slot;
    }

    public Direcciones setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public int getmFlagImage() {
        return mFlagImage;
    }

    public Direcciones setmFlagImage(int mFlagImage) {
        this.mFlagImage = mFlagImage;
        return this;
    }

    public Direcciones(Direcciones direcciones)
    {
        etiqueta = direcciones.getEtiqueta();
        calle = direcciones.getCalle();
        colonia = direcciones.getColonia();
        codigo_postal = direcciones.getCodigo_postal();
        domicilioLargo = direcciones.getDomicilioLargo();
        numExterior = direcciones.getNumExterior();
        numInterior = direcciones.getNumInterior();
        SParticular = direcciones.getSParticular();
        empresa = direcciones.getEmpresa();
        ruta = direcciones.getRuta();
        mLatLng = direcciones.getmLatLng();
        slot = direcciones.getSlot();
        mFlagImage = 0;

        mRuta = new Ruta();
    }

    public Direcciones()
    {
        etiqueta = "";
        calle = "";
        colonia = "";
        codigo_postal = "";
        domicilioLargo = "";
        numExterior = "";
        numInterior = "";
        SParticular = "";
        empresa = 0;
        ruta = "";
        mLatLng = new LatLng(0,0);
        slot = 0;

        int flagImage = this.empresa;
        if(flagImage==0)
            flagImage = R.drawable.logo_zetagas;
        if(flagImage==1) //Thermo
            flagImage = R.drawable.logo_th;
        if(flagImage==2) //Multigas
            flagImage = R.drawable.logo_mg;
        if(flagImage==3) //Gas Licuado
            flagImage = R.drawable.logo_gl;

        mFlagImage = flagImage;

        mRuta = new Ruta();
    }
    public Direcciones(String etiqueta, String calle, String colonia, String codigo_postal, String domicilioLargo, String numExterior, String numInterior, String SParticular, int empresa, String ruta, LatLng mLatLng, int slot) {
        this.etiqueta = etiqueta;
        this.calle = calle;
        this.colonia = colonia;
        this.codigo_postal = codigo_postal;
        this.domicilioLargo = domicilioLargo;
        this.numExterior = numExterior;
        this.numInterior = numInterior;
        this.SParticular = SParticular;
        this.empresa = empresa;
        this.ruta = ruta;
        this.mLatLng = mLatLng;
        this.slot = slot;

        int flagImage = this.empresa;
        if(flagImage==0)
            flagImage = R.drawable.logo_zetagas;
        if(flagImage==1) //Thermo
            flagImage = R.drawable.logo_th;
        if(flagImage==2) //Multigas
            flagImage = R.drawable.logo_mg;
        if(flagImage==3) //Gas Licuado
            flagImage = R.drawable.logo_gl;

        mFlagImage = flagImage;

        mRuta = new Ruta();
    }
}
