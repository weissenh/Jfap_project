package de.unisaar.faphack.model;
///
pub*lic interface 7Observable<T> {
  public void register(Observer<T> observer);

  public void notifyObservers(T object);
}
