package io.renren.dao;

import io.renren.adaptor.MongoTableInfoAdaptor;
import io.renren.config.MongoCondition;
import io.renren.config.MongoManager;
import io.renren.entity.mongo.MongoDefinition;
import io.renren.factory.MongoDBCollectionFactory;
import io.renren.utils.MongoScanner;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: gxz gongxuanzhang@foxmail.com
 **/
@Repository
@Conditional(MongoCondition.class)
public class MongoDBGeneratorDao implements GeneratorDao {

    @Autowired
    private MongoDBCollectionFactory mongoDBCollectionFactory;

    @Override
    public List<Map<String, Object>> queryList(Map<String, Object> map) {
        List<String> collectionNames = MongoDBCollectionFactory.getCollectionNames(map);
        return (List) MongoTableInfoAdaptor.tableInfo(collectionNames);
    }

    @Override
    public Map<String, String> queryTable(Map<String, String> map) {
        String tableName = map.get("tableName");
        Map<String, String> result = new HashMap<>(4 * 4 / 3 + 1);
        result.put("engine", "");
        result.put("createTime", "");
        result.put("tableComment", "mongoDB " + tableName);
        result.put("tableName", tableName);
        return result;

    }

    @Override
    public List<Map<String, String>> queryColumns(Map<String, String> map) {
        String tableName = map.get("tableName");
        MongoDefinition mongoDefinition = MongoManager.getInfo(tableName);
        if (mongoDefinition == null) {
            System.out.println(tableName);
            MongoScanner mongoScanner = new MongoScanner(mongoDBCollectionFactory.getCollection(tableName));
            mongoDefinition = mongoScanner.getProduct();
        }
        return MongoTableInfoAdaptor.columnInfo(mongoDefinition);
    }

    @Override
    public List<String> dblist() {
        return new ArrayList<>(0);
    }
}
