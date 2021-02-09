package zgas.client.models;

import com.google.android.gms.maps.model.LatLng;

public class ClientBooking{
    String SParticular;
    String apellidoOperador;
    String calle;
    int cantidad10kg;
    int cantidad20kg;
    int cantidad30kg;
    String codigo_postal;
    String colonia;
    String direccion;
    int empresa;
    String etiqueta;
    String id;
    String idCliente;
    String idDriver;
    double latitud;
    double longitud;
    String nombreOperador;
    String numExterior;
    String numInterior;
    String ruta;
    String status;
    String total;
    String type;
    String unidadOperador;

    public String getIdHistoryBooking() {
        return idHistoryBooking;
    }

    public ClientBooking setIdHistoryBooking(String idHistoryBooking) {
        this.idHistoryBooking = idHistoryBooking;
        return this;
    }

    String idHistoryBooking;



    public ClientBooking(){}

    public ClientBooking(String idClient, String idDriver, String id, String type, String status, int cantidad30kg, int cantidad20kg, int cantidad10kg, Direcciones direcciones) {
        this.idCliente = idClient;
        this.idDriver = idDriver;
        this.id = id;
        this.type = type;
        this.status = status;
        this.cantidad30kg = cantidad30kg;
        this.cantidad20kg = cantidad20kg;
        this.cantidad10kg = cantidad10kg;

        this.SParticular = direcciones.getSParticular();
        this.calle = direcciones.getCalle();
        this.codigo_postal = direcciones.getCodigo_postal();
        this.colonia = direcciones.getColonia();
        this.direccion = direcciones.getDomicilioLargo();
        this.empresa = direcciones.getEmpresa();
        this.etiqueta = direcciones.getEtiqueta();
        this.latitud = direcciones.getmLatLng().latitude;
        this.longitud = direcciones.getmLatLng().longitude;
        this.numExterior = direcciones.getNumExterior();
        this.numInterior = direcciones.getNumInterior();
        this.ruta = direcciones.getRuta();
    }



    public boolean isValData(int posicion)
    {
        return cantidad30kg != 100 && cantidad20kg != 100 && cantidad10kg != 100 && posicion != 100 && id != null && type != null && status != null;
    }

    public String getSParticular() {
        return SParticular;
    }

    public ClientBooking setSParticular(String SParticular) {
        this.SParticular = SParticular;
        return this;
    }

    public String getApellidoOperador() {
        return apellidoOperador;
    }

    public ClientBooking setApellidoOperador(String apellidoOperador) {
        this.apellidoOperador = apellidoOperador;
        return this;
    }

    public String getCalle() {
        return calle;
    }

    public ClientBooking setCalle(String calle) {
        this.calle = calle;
        return this;
    }

    public int getCantidad10kg() {
        return cantidad10kg;
    }

    public ClientBooking setCantidad10kg(int cantidad10kg) {
        this.cantidad10kg = cantidad10kg;
        return this;
    }

    public int getCantidad20kg() {
        return cantidad20kg;
    }

    public ClientBooking setCantidad20kg(int cantidad20kg) {
        this.cantidad20kg = cantidad20kg;
        return this;
    }

    public int getCantidad30kg() {
        return cantidad30kg;
    }

    public ClientBooking setCantidad30kg(int cantidad30kg) {
        this.cantidad30kg = cantidad30kg;
        return this;
    }

    public String getCodigo_postal() {
        return codigo_postal;
    }

    public ClientBooking setCodigo_postal(String codigo_postal) {
        this.codigo_postal = codigo_postal;
        return this;
    }

    public String getColonia() {
        return colonia;
    }

    public ClientBooking setColonia(String colonia) {
        this.colonia = colonia;
        return this;
    }

    public String getDireccion() {
        return direccion;
    }

    public ClientBooking setDireccion(String direccion) {
        this.direccion = direccion;
        return this;
    }

    public int getEmpresa() {
        return empresa;
    }

    public ClientBooking setEmpresa(int empresa) {
        this.empresa = empresa;
        return this;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public ClientBooking setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
        return this;
    }

    public String getId() {
        return id;
    }

    public ClientBooking setId(String id) {
        this.id = id;
        return this;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public ClientBooking setIdCliente(String idCliente) {
        this.idCliente = idCliente;
        return this;
    }

    public String getIdDriver() {
        return idDriver;
    }

    public ClientBooking setIdDriver(String idDriver) {
        this.idDriver = idDriver;
        return this;
    }

    public double getLatitud() {
        return latitud;
    }

    public ClientBooking setLatitud(double latitud) {
        this.latitud = latitud;
        return this;
    }

    public double getLongitud() {
        return longitud;
    }

    public ClientBooking setLongitud(double longitud) {
        this.longitud = longitud;
        return this;
    }

    public String getNombreOperador() {
        return nombreOperador;
    }

    public ClientBooking setNombreOperador(String nombreOperador) {
        this.nombreOperador = nombreOperador;
        return this;
    }

    public String getNumExterior() {
        return numExterior;
    }

    public ClientBooking setNumExterior(String numExterior) {
        this.numExterior = numExterior;
        return this;
    }

    public String getNumInterior() {
        return numInterior;
    }

    public ClientBooking setNumInterior(String numInterior) {
        this.numInterior = numInterior;
        return this;
    }

    public String getRuta() {
        return ruta;
    }

    public ClientBooking setRuta(String ruta) {
        this.ruta = ruta;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public ClientBooking setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getTotal() {
        return total;
    }

    public ClientBooking setTotal(String total) {
        this.total = total;
        return this;
    }

    public String getType() {
        return type;
    }

    public ClientBooking setType(String type) {
        this.type = type;
        return this;
    }

    public String getUnidadOperador() {
        return unidadOperador;
    }

    public ClientBooking setUnidadOperador(String unidadOperador) {
        this.unidadOperador = unidadOperador;
        return this;
    }
}
