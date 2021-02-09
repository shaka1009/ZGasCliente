package zgas.client.models;

import java.text.DecimalFormat;

import zgas.client.Home;

public class Price {


    private static boolean isLoad = false; //Llave de carga

    private double thermogas;
    private double multigas;
    private double gaslicuado;

    public Price(){}

    public Price(double thermogas, double multigas, double gaslicuado) {
        this.thermogas = thermogas; // 1 - Thermogas
        this.multigas = multigas;   // 2 - Multigas
        this.gaslicuado = gaslicuado; //3 GasLicuado
    }

    public String getPrecio(int empresa, int kg) {

        DecimalFormat df= new DecimalFormat("#.00");
        if (empresa == 1)
            return "$" + df.format(Home.mPrice.getThermogas() * kg);
        if (empresa == 2)
            return "$" + df.format(Home.mPrice.getMultigas() * kg);
        if (empresa == 3)
            return "$" + df.format(Home.mPrice.getGaslicuado() * kg);
        else
            return "$" + df.format(Home.mPrice.getThermogas() * kg);
    }

    public String getTotal(int empresa, int cantidad30Kg, int cantidad20Kg, int cantidad10Kg) {
        int total = (cantidad30Kg*30) + (cantidad20Kg*20) + (cantidad10Kg*10);
        DecimalFormat df= new DecimalFormat("#.00");


        if(cantidad30Kg==0 && cantidad20Kg==0 && cantidad10Kg==0)
            return "$00.00";
        else if (empresa == 1)
            return "$" + df.format(Home.mPrice.getThermogas() * total);
        else if (empresa == 2)
            return "$" + df.format(Home.mPrice.getMultigas() * total);
        else if (empresa == 3)
            return "$" + df.format(Home.mPrice.getGaslicuado() * total);
        else
            return "$" + df.format(Home.mPrice.getThermogas() * total);
    }

    public static boolean isLoad() {
        return isLoad;
    }

    public static void setIsLoad(boolean isLoad) {
        Price.isLoad = isLoad;
    }

    public double getThermogas() {
        return thermogas;
    }

    public Price setThermogas(double thermogas) {
        this.thermogas = thermogas;
        return this;
    }

    public double getMultigas() {
        return multigas;
    }

    public Price setMultigas(double multigas) {
        this.multigas = multigas;
        return this;
    }

    public double getGaslicuado() {
        return gaslicuado;
    }

    public Price setGaslicuado(double gaslicuado) {
        this.gaslicuado = gaslicuado;
        return this;
    }
}
