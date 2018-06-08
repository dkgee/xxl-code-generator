import java.util.Map;
import java.util.List;

/**
* 描述：${classInfo.classComment}
* 作者：JinHuaTao
* 时间：${.now?string('yyyy-MM-dd HH:mm:ss')}
*/
public interface ${classInfo.className}Service {

    /**
    * 新增
    */
    public ReturnT<String> insert(${classInfo.className} ${classInfo.className?uncap_first});

    /**
    * 批量新增
    */
    public void batchInsert(List<${classInfo.className}> list);

    /**
    * 删除
    */
    public ReturnT<String> delete(int id);

    /**
    * 更新
    */
    public ReturnT<String> update(${classInfo.className} ${classInfo.className?uncap_first});

    /**
    * Load查询
    */
    public ${classInfo.className} load(int id);

    /**
    * 分页查询
    */
    public Map<String,Object> pageList(int offset, int pagesize);

}
