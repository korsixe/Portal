public interface IAd { // Интерфейс для получения данных объявления

  String getTitle();

  String getDescription();

  String getCategory();

  String getCondition();

  boolean isNegotiablePrice();

  int getPrice();

  String getLocation();

  int getIdUser();

  String getStatus();

  String toString();
}
