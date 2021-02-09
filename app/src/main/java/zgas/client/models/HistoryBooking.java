package zgas.client.models;

public class HistoryBooking {
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

    public HistoryBooking() {

    }

    public HistoryBooking(String sparticular, String apellidoOperador, String calle, int cantidad10kg, int cantidad20kg, int cantidad30kg, String codigo_postal, String colonia, String direccion, int empresa, String etiqueta, String id, String idCliente, String idDriver, double latitud, double longitud, String nombreOperador, String numExterior, String numInterior, String ruta, String status, String total, String type, String unidadOperador, String idHistoryBooking, double calificationClient, double calificationDriver, long timestamp) {
        this.SParticular = sparticular;
        this.apellidoOperador = apellidoOperador;
        this.calle = calle;
        this.cantidad10kg = cantidad10kg;
        this.cantidad20kg = cantidad20kg;
        this.cantidad30kg = cantidad30kg;
        this.codigo_postal = codigo_postal;
        this.colonia = colonia;
        this.direccion = direccion;
        this.empresa = empresa;
        this.etiqueta = etiqueta;
        this.id = id;
        this.idCliente = idCliente;
        this.idDriver = idDriver;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombreOperador = nombreOperador;
        this.numExterior = numExterior;
        this.numInterior = numInterior;
        this.ruta = ruta;
        this.status = status;
        this.total = total;
        this.type = type;
        this.unidadOperador = unidadOperador;
        this.idHistoryBooking = idHistoryBooking;
        this.calificationClient = calificationClient;
        this.calificationDriver = calificationDriver;
        this.timestamp = timestamp;
    }

    double longitud;
    String nombreOperador;
    String numExterior;
    String numInterior;
    String ruta;
    String status;
    String total;
    String type;
    String unidadOperador;
    String idHistoryBooking;

    double calificationClient;
    double calificationDriver;
    long timestamp;

    public HistoryBooking(ClientBooking clientBooking)
    {
        SParticular = clientBooking.getSParticular();
        apellidoOperador = clientBooking.getApellidoOperador();
        calle = clientBooking.getCalle();
        cantidad10kg = clientBooking.getCantidad10kg();
        cantidad20kg = clientBooking.getCantidad20kg();
        cantidad30kg = clientBooking.getCantidad30kg();
        codigo_postal = clientBooking.getCodigo_postal();
        colonia = clientBooking.getColonia();
        direccion = clientBooking.getDireccion();
        empresa = clientBooking.getEmpresa();
        etiqueta = clientBooking.getEtiqueta();
        id = clientBooking.getId();
        idCliente = clientBooking.getIdCliente();
        idDriver = clientBooking.getIdDriver();
        latitud = clientBooking.getLatitud();
        longitud = clientBooking.getLongitud();
        nombreOperador = clientBooking.getNombreOperador();
        numExterior = clientBooking.getNumExterior();
        numInterior = clientBooking.getNumInterior();
        ruta = clientBooking.getRuta();
        status = clientBooking.getStatus();
        total = clientBooking.getTotal();
        type = clientBooking.getType();
        unidadOperador = clientBooking.getUnidadOperador();
    }

    public String getSParticular() {
        return SParticular;
    }

    public HistoryBooking setSParticular(String SParticular) {
        this.SParticular = SParticular;
        return this;
    }

    public String getApellidoOperador() {
        return apellidoOperador;
    }

    public HistoryBooking setApellidoOperador(String apellidoOperador) {
        this.apellidoOperador = apellidoOperador;
        return this;
    }

    public String getCalle() {
        return calle;
    }

    public HistoryBooking setCalle(String calle) {
        this.calle = calle;
        return this;
    }

    public int getCantidad10kg() {
        return cantidad10kg;
    }

    public HistoryBooking setCantidad10kg(int cantidad10kg) {
        this.cantidad10kg = cantidad10kg;
        return this;
    }

    public int getCantidad20kg() {
        return cantidad20kg;
    }

    public HistoryBooking setCantidad20kg(int cantidad20kg) {
        this.cantidad20kg = cantidad20kg;
        return this;
    }

    public int getCantidad30kg() {
        return cantidad30kg;
    }

    public HistoryBooking setCantidad30kg(int cantidad30kg) {
        this.cantidad30kg = cantidad30kg;
        return this;
    }

    public String getCodigo_postal() {
        return codigo_postal;
    }

    public HistoryBooking setCodigo_postal(String codigo_postal) {
        this.codigo_postal = codigo_postal;
        return this;
    }

    public String getColonia() {
        return colonia;
    }

    public HistoryBooking setColonia(String colonia) {
        this.colonia = colonia;
        return this;
    }

    public String getDireccion() {
        return direccion;
    }

    public HistoryBooking setDireccion(String direccion) {
        this.direccion = direccion;
        return this;
    }

    public int getEmpresa() {
        return empresa;
    }

    public HistoryBooking setEmpresa(int empresa) {
        this.empresa = empresa;
        return this;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public HistoryBooking setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
        return this;
    }

    public String getId() {
        return id;
    }

    public HistoryBooking setId(String id) {
        this.id = id;
        return this;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public HistoryBooking setIdCliente(String idCliente) {
        this.idCliente = idCliente;
        return this;
    }

    public String getIdDriver() {
        return idDriver;
    }

    public HistoryBooking setIdDriver(String idDriver) {
        this.idDriver = idDriver;
        return this;
    }

    public double getLatitud() {
        return latitud;
    }

    public HistoryBooking setLatitud(double latitud) {
        this.latitud = latitud;
        return this;
    }

    public double getLongitud() {
        return longitud;
    }

    public HistoryBooking setLongitud(double longitud) {
        this.longitud = longitud;
        return this;
    }

    public String getNombreOperador() {
        return nombreOperador;
    }

    public HistoryBooking setNombreOperador(String nombreOperador) {
        this.nombreOperador = nombreOperador;
        return this;
    }

    public String getNumExterior() {
        return numExterior;
    }

    public HistoryBooking setNumExterior(String numExterior) {
        this.numExterior = numExterior;
        return this;
    }

    public String getNumInterior() {
        return numInterior;
    }

    public HistoryBooking setNumInterior(String numInterior) {
        this.numInterior = numInterior;
        return this;
    }

    public String getRuta() {
        return ruta;
    }

    public HistoryBooking setRuta(String ruta) {
        this.ruta = ruta;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public HistoryBooking setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getTotal() {
        return total;
    }

    public HistoryBooking setTotal(String total) {
        this.total = total;
        return this;
    }

    public String getType() {
        return type;
    }

    public HistoryBooking setType(String type) {
        this.type = type;
        return this;
    }

    public String getUnidadOperador() {
        return unidadOperador;
    }

    public HistoryBooking setUnidadOperador(String unidadOperador) {
        this.unidadOperador = unidadOperador;
        return this;
    }

    public String getIdHistoryBooking() {
        return idHistoryBooking;
    }

    public HistoryBooking setIdHistoryBooking(String idHistoryBooking) {
        this.idHistoryBooking = idHistoryBooking;
        return this;
    }

    public double getCalificationClient() {
        return calificationClient;
    }

    public HistoryBooking setCalificationClient(double calificationClient) {
        this.calificationClient = calificationClient;
        return this;
    }

    public double getCalificationDriver() {
        return calificationDriver;
    }

    public HistoryBooking setCalificationDriver(double calificationDriver) {
        this.calificationDriver = calificationDriver;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public HistoryBooking setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}
