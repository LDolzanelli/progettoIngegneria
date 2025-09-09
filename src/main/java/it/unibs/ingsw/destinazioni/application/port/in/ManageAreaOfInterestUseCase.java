package it.unibs.ingsw.destinazioni.application.port.in;

/**
 * Interface per la gestione delle aree di interesse nel sistema.
 */
/*@ public invariant (\forall String town; townList().contains(town);
  @                   town != null && !town.trim().isEmpty());
  @ public invariant (\forall String province; townProvinceMap().values().contains(province);
  @                   province != null && !province.trim().isEmpty());
  @*/
public interface ManageAreaOfInterestUseCase {

  /**
   * Aggiunge una nuova area di interesse specificando comune e provincia.
   * 
   * @param townName nome del comune
   * @param provinceName nome della provincia
   * @throws IllegalArgumentException se i parametri sono null o vuoti
   */
  /*@ requires townName != null && !townName.trim().isEmpty();
    @ requires provinceName != null && !provinceName.trim().isEmpty();
    @ ensures townList().contains(townName);
    @ ensures townProvinceMap().containsKey(townName);
    @ ensures townProvinceMap().get(townName).equals(provinceName);
    @ ensures !isEmpty();
    @ signals (IllegalArgumentException e) townName == null || townName.trim().isEmpty() ||
    @                                      provinceName == null || provinceName.trim().isEmpty();
    @*/
  void addArea(String townName, String provinceName);
}
