/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package x7.repository;

import x7.core.bean.Criteria;
import x7.core.bean.Parser;
import x7.core.bean.Transformed;
import x7.core.bean.condition.InCondition;
import x7.core.bean.condition.ReduceCondition;
import x7.core.bean.condition.RefreshCondition;
import x7.core.web.Direction;
import x7.core.web.Page;
import x7.repository.dao.Dao;
import x7.repository.schema.SchemaConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SqlDataTransform implements DataTransform{

    private Dao dao;
    public void setDao(Dao dao){
        this.dao = dao;
    }

    @Deprecated
    @Override
    public <T> void refreshCache(Class<T> clz) {
        throw new RuntimeException("Wrong Code");
    }

    @Override
    public long create(Object obj) {

        if (!SchemaConfig.isSchemaTransformEnabled)
            return this.dao.create(obj);

        Transformed transformed = Parser.transform(obj);

        return this.dao.create(transformed);
    }

    @Override
    public boolean refresh(Object obj) {
        if (!SchemaConfig.isSchemaTransformEnabled)
            return this.dao.refresh(obj);

        Transformed transformed = Parser.transform(obj);

        return this.dao.refresh(transformed);
    }

    @Override
    public <T> boolean refresh(RefreshCondition<T> refreshCondition) {

        if (!SchemaConfig.isSchemaTransformEnabled)
            return this.dao.refreshByCondition(refreshCondition);

        RefreshCondition refreshConditionTransformed = new RefreshCondition();
        refreshConditionTransformed.setClz(refreshCondition.getClz());
        refreshConditionTransformed.setRefreshList(refreshCondition.getRefreshList());
        refreshConditionTransformed.setCondition(refreshCondition.getCondition());
        refreshConditionTransformed.setSourceStript(refreshCondition.getSourceStript());

        Object obj = refreshCondition.getObj();
        if (Objects.nonNull(obj)) {
            Transformed transformed = Parser.transform(obj);
            refreshConditionTransformed.setObj(transformed);
        }

        return this.dao.refreshByCondition(refreshConditionTransformed);
    }

    @Override
    public boolean remove(Object obj) {
        if (!SchemaConfig.isSchemaTransformEnabled)
            return this.dao.remove(obj);

        Transformed transformed = Parser.transform(obj);
        return this.dao.remove(transformed);
    }

    @Override
    public boolean createBatch(List<?> objList) {
        if (!SchemaConfig.isSchemaTransformEnabled)
            return this.dao.createBatch(objList);

        List<Transformed> list = new ArrayList<>();
        for (Object obj : objList){
            Transformed tf = Parser.transform(obj);
            list.add(tf);
        }

        return this.dao.createBatch(list);
    }

    @Override
    public <T> boolean execute(T obj, String sql) {
        if (!SchemaConfig.isSchemaTransformEnabled)
            return this.dao.execute(obj,sql);

        Transformed transformed = Parser.transform(obj);
        return this.dao.execute(transformed, sql);
    }

    @Override
    public <T> T get(Class<T> clz, long idOne) {
        if (!SchemaConfig.isSchemaTransformEnabled)
            return this.dao.get(clz,idOne);

        Class clzz = Parser.transformClzz(clz);

        Object obj = this.dao.get(clzz, idOne);
        if (Objects.isNull(obj))
            return null;

        T t = Parser.toLogic((Transformed)obj,clz);
        return t;
    }

    @Override
    public <T> List<T> list(Object obj) {
        if (!SchemaConfig.isSchemaTransformEnabled)
            return this.dao.list(obj);

        Transformed transformed = Parser.transform(obj);

        List<Transformed> transformedList = this.dao.list(transformed);

        Class<T> clzz = (Class<T>) obj.getClass();
        List<T> list = new ArrayList<>();
        for (Transformed tf : transformedList) {
            T t = Parser.toLogic(tf, clzz);
            list.add(t);
        }

        return list;
    }

    @Override
    public <T> List<T> list(Class<T> clz) {
        if (!SchemaConfig.isSchemaTransformEnabled)
            return this.dao.list(clz);

        Class<? extends Transformed> clzz = Parser.transformClzz(clz);
        List<? extends  Transformed> transformedList = this.dao.list(clzz);

        List<T> list = new ArrayList<>();
        for (Transformed tf : transformedList) {
            T t = Parser.toLogic(tf, clz);
            list.add(t);
        }

        return list;
    }

    @Override
    public <T> T getOne(T obj) {
        if (!SchemaConfig.isSchemaTransformEnabled)
            return this.dao.getOne(obj);

        Transformed transformed = Parser.transform(obj);
        Transformed tf = this.dao.getOne(transformed);
        if (Objects.isNull(tf))
            return null;
        T t = Parser.toLogic(tf, (Class<T>) obj.getClass());
        return t;
    }

    @Override
    public <T> T getOne(T obj, String orderBy, Direction sc) {
        if (!SchemaConfig.isSchemaTransformEnabled)
            return this.dao.getOne(obj,orderBy,sc);

        Transformed transformed = Parser.transform(obj);
        Transformed tf = this.dao.getOne(transformed, orderBy, sc);
        if (Objects.isNull(tf))
            return null;
        T t = Parser.toLogic(tf, (Class<T>) obj.getClass());
        return t;
    }


    @Override
    public <T> List<T> in(InCondition inCondition) {
        return this.dao.in(inCondition);
    }

    @Override
    public Object reduce(ReduceCondition reduceCondition) {
        return this.dao.reduce(reduceCondition);
    }

    @Override
    public <T> Page<T> find(Criteria criteria) {
        return this.dao.find(criteria);
    }

    @Override
    public Page<Map<String, Object>> find(Criteria.ResultMappedCriteria resultMapped) {
        return this.dao.find(resultMapped);
    }

    @Override
    public List<Map<String, Object>> list(Criteria.ResultMappedCriteria resultMapped) {
        return this.dao.list(resultMapped);
    }

    @Override
    public <T> List<T> list(Criteria criteria) {
        return this.dao.list(criteria);
    }


    @Override
    public List<Map<String, Object>> list(Class clz, String sql, List<Object> conditionList) {
        return this.dao.list(clz,sql,conditionList);
    }
}
