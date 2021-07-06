/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.renren.config.MongoManager;
import io.renren.dao.GeneratorDao;
import io.renren.dao.MongoDBGeneratorDao;
import io.renren.entity.mongo.MongoDefinition;
import io.renren.factory.MongoDBCollectionFactory;
import io.renren.utils.GenUtils;
import io.renren.utils.PageUtils;
import io.renren.utils.Query;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class SysGeneratorService {
    @Autowired
    private GeneratorDao generatorDao;

    public List<String> dblist() {
        return generatorDao.dblist();
    }


    public PageUtils queryList(Query query) {
        Page<?> page = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map<String, Object>> list = generatorDao.queryList(query);
        int total = (int) page.getTotal();
        if (generatorDao instanceof MongoDBGeneratorDao) {
            total = MongoDBCollectionFactory.getCollectionTotal(query);
        }
        return new PageUtils(list, total, query.getLimit(), query.getPage());
    }

    public Map<String, String> queryTable(String dbName,String tableName) {
        Map<String,String> map = new HashMap<>(2);
        map.put("dbName",dbName);
        map.put("tableName",tableName);
        return generatorDao.queryTable(map);
    }

    public List<Map<String, String>> queryColumns(String dbName,String tableName) {
        Map<String,String> map = new HashMap<>(2);
        map.put("dbName",dbName);
        map.put("tableName",tableName);
        return generatorDao.queryColumns(map);
    }


    public byte[] generatorCode(String dbName, String[] tableNames) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (String tableName : tableNames) {
            //查询表信息
            Map<String, String> table = queryTable(dbName,tableName);
            //查询列信息
            List<Map<String, String>> columns = queryColumns(dbName,tableName);
            //生成代码
            GenUtils.generatorCode(dbName,table, columns, zip);
        }
        if (MongoManager.isMongo()) {
            GenUtils.generatorMongoCode(dbName,tableNames, zip);
        }


        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }
}
