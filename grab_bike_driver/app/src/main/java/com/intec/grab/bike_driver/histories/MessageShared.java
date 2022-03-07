package com.intec.grab.bike_driver.histories;

public class MessageShared
{
    public static String RenderTextFromOrderStatus(String _text) {
        if (_text.equalsIgnoreCase("PENDING")) return "<span style='color:#ff0000'><b>PENDING</b></span>";
        if (_text.equalsIgnoreCase("ACCEPTED")) return "<span style='color:#ff0000'><b>ACCEPTED</b></span>";
        if (_text.equalsIgnoreCase("STARTED")) return "<span style='color:#ff0000'><b>STARTED</b></span>";
        if (_text.equalsIgnoreCase("END")) return "<span style='color:#ff0000'><b>END</b></span>";
        if (_text.equalsIgnoreCase("EVALUATION")) return "<span style='color:#ff0000'><b>EVALUATION</b></span>";

        if (_text.equalsIgnoreCase("CANCEL_BY_USER")) return "<span style='color:#ff0000'><b>CANCEL BY USER</b></span>";
        if (_text.equalsIgnoreCase("CANCEL_BY_DRIVER")) return "<span style='color:#ff0000'><b>CANCEL BY DRIVER</b></span>";
        if (_text.equalsIgnoreCase("CANCEL_BY_ADMIN")) return "<span style='color:#ff0000'><b>CANCEL BY ADMIN</b></span>";
        if (_text.equalsIgnoreCase("CANCEL_BY_SYSTEM")) return "<span style='color:#ff0000'><b>CANCEL BY SYSTEM</b></span>";
        return "UN_KNOWN";
    }

    public static String RenderEnumFromOrderStatus(String _enum) {
        if (_enum.equals(MessageStatus.PENDING)) return "<span style='color:#ff0000'><b>PENDING</b></span>";
        if (_enum.equals(MessageStatus.ACCEPTED)) return "<span style='color:#ff0000'><b>ACCEPTED</b></span>";
        if (_enum.equals(MessageStatus.STARTED)) return "<span style='color:#ff0000'><b>STARTED</b></span>";
        if (_enum.equals(MessageStatus.END)) return "<span style='color:#ff0000'><b>END</b></span>";
        if (_enum.equals(MessageStatus.EVALUATION)) return "<span style='color:#ff0000'><b>EVALUATION</b></span>";

        if (_enum.equals(MessageStatus.CANCEL_BY_USER)) return "<span style='color:#ff0000'><b>CANCEL BY USER</b></span>";
        if (_enum.equals(MessageStatus.CANCEL_BY_DRIVER)) return "<span style='color:#ff0000'><b>CANCEL BY DRIVER</b></span>";
        if (_enum.equals(MessageStatus.CANCEL_BY_ADMIN)) return "<span style='color:#ff0000'><b>CANCEL BY ADMIN</b></span>";
        if (_enum.equals(MessageStatus.CANCEL_BY_SYSTEM)) return "<span style='color:#ff0000'><b>CANCEL BY SYSTEM</b></span>";
        return "UN_KNOWN";
    }
}
