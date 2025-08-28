package mcv.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Assunzione {
    private final String codiceFiscale;
    private final StringProperty farmaco;
    private final StringProperty dosaggio;
    private final ObjectProperty<LocalDateTime> dataOra;

    public Assunzione(String codiceFiscale, String farmaco, String dosaggio, LocalDateTime dataOra) {
        this.codiceFiscale = codiceFiscale;
        this.farmaco = new SimpleStringProperty(farmaco);
        this.dosaggio = new SimpleStringProperty(dosaggio);
        this.dataOra = new SimpleObjectProperty<>(dataOra);
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public String getFarmaco() {
        return farmaco.get();
    }

    public String getDosaggio() {
        return dosaggio.get();
    }

    public LocalDateTime getDataOra() {
        return dataOra.get();
    }

    public StringProperty farmacoProperty() {
        return farmaco;
    }

    public StringProperty dosaggioProperty() {
        return dosaggio;
    }

    public ObjectProperty<LocalDateTime> dataOraProperty() {
        return dataOra;
    }
}
