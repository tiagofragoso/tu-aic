package group3.aic_middleware.exceptions;

public class DeviceNotFoundException extends Exception {

    public DeviceNotFoundException (String errorMessage) {
        super(errorMessage);
    }

}
