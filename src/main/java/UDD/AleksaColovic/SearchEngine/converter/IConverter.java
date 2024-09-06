package UDD.AleksaColovic.SearchEngine.converter;

public interface IConverter<C, D> {
    public C toClass(D dto);
    public D toDto(C domainClass);
}
