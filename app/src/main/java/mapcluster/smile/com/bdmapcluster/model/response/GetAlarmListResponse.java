package mapcluster.smile.com.bdmapcluster.model.response;

import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xh_smile on 2019/11/12.
 */

public class GetAlarmListResponse implements Serializable {
    /**
     * code : 200
     * data : {"number":1,"total":14912,"page":1,"list":[{"policeId":"706","vertices":"673,322@837,1328","px":"114.0493837042","py":"22.720067864","threshold":"95","orgId":null,"peopleId":"651D03A9C277418BBF5438CF119DB88C","orgCode":null,"alarmId":"8d38ba90289d4cc38c11b2db96cedbe8","timeDesc":"1小时前","cameraName":"观澜中心小学门口枪","stationId":"26985","gbCode":"44031158001320029186","comments":"情报人员库","libId":"110","orgName":"龙华分局观澜派出所","cilNames":"","alarmTime":"2019-11-12 10:38:58","libName":"情报人员库","smallHttpUrl":"","dataStatus":"","parentId":null,"url":"","cameraId":"06bd766a5a9e4bcca6e3791471d8af68","bigHttpUrl":"","idcard":"440321197810033325","name":"戴金梅","cameraCode":null,"status":"0"}],"size":1,"totalPage":14912}
     * message : 请求成功
     */

    private String code;
    private DataBean data;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        if (TextUtils.isEmpty(message)) {
            return "服务器返回的数据为空";
        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBean implements Serializable {
        /**
         * number : 1
         * total : 14912
         * page : 1
         * list : [{"policeId":"706","vertices":"673,322@837,1328","px":"114.0493837042","py":"22.720067864","threshold":"95","orgId":null,"peopleId":"651D03A9C277418BBF5438CF119DB88C","orgCode":null,"alarmId":"8d38ba90289d4cc38c11b2db96cedbe8","timeDesc":"1小时前","cameraName":"观澜中心小学门口枪","stationId":"26985","gbCode":"44031158001320029186","comments":"情报人员库","libId":"110","orgName":"龙华分局观澜派出所","cilNames":"","alarmTime":"2019-11-12 10:38:58","libName":"情报人员库","smallHttpUrl":"","dataStatus":"","parentId":null,"url":"","cameraId":"06bd766a5a9e4bcca6e3791471d8af68","bigHttpUrl":"","idcard":"440321197810033325","name":"戴金梅","cameraCode":null,"status":"0"}]
         * size : 1
         * totalPage : 14912
         */

        private int number;
        private int total;
        private int page;
        private int size;
        private int totalPage;
        private List<ListBean> list;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean implements Serializable, MultiItemEntity {
            /**
             * policeId : 706
             * vertices : 673,322@837,1328
             * px : 114.0493837042
             * py : 22.720067864
             * threshold : 95
             * orgId : null
             * peopleId : 651D03A9C277418BBF5438CF119DB88C
             * orgCode : null
             * alarmId : 8d38ba90289d4cc38c11b2db96cedbe8
             * timeDesc : 1小时前
             * cameraName : 观澜中心小学门口枪
             * stationId : 26985
             * gbCode : 44031158001320029186
             * comments : 情报人员库
             * libId : 110
             * orgName : 龙华分局观澜派出所
             * cilNames :
             * alarmTime : 2019-11-12 10:38:58
             * libName : 情报人员库
             * smallHttpUrl :
             * dataStatus :
             * parentId : null
             * url :
             * cameraId : 06bd766a5a9e4bcca6e3791471d8af68
             * bigHttpUrl :
             * idcard : 440321197810033325
             * name : 戴金梅
             * cameraCode : null
             * status : 0
             */

            private String policeId;
            private String vertices;
            private String px;
            private String py;
            private String threshold;
            private Object orgId;
            private String peopleId;
            private Object orgCode;
            private String alarmId;
            private String timeDesc;
            private String cameraName;
            private String stationId;
            private String gbCode;
            private String comments;
            private String libId;
            private String orgName;
            private String cilNames;//某个布控库的子库名称
            private String alarmTime;
            private String libName;
            private String smallHttpUrl;
            private String dataStatus;
            private Object parentId;
            private String url;
            private String cameraId;
            private String bigHttpUrl;
            private String idcard;
            private String name;
            private Object cameraCode;
            private String status;

            /**
             * 1-按人员聚合展示(按对象)； 2-按时间展示；
             */
            private int itemType;

            public String getPoliceId() {
                return policeId;
            }

            public void setPoliceId(String policeId) {
                this.policeId = policeId;
            }

            public String getVertices() {
                return vertices;
            }

            public void setVertices(String vertices) {
                this.vertices = vertices;
            }

            public String getPx() {
                return px;
            }

            public void setPx(String px) {
                this.px = px;
            }

            public String getPy() {
                return py;
            }

            public void setPy(String py) {
                this.py = py;
            }

            public String getThreshold() {
                return threshold;
            }

            public void setThreshold(String threshold) {
                this.threshold = threshold;
            }

            public Object getOrgId() {
                return orgId;
            }

            public void setOrgId(Object orgId) {
                this.orgId = orgId;
            }

            public String getPeopleId() {
                return peopleId;
            }

            public void setPeopleId(String peopleId) {
                this.peopleId = peopleId;
            }

            public Object getOrgCode() {
                return orgCode;
            }

            public void setOrgCode(Object orgCode) {
                this.orgCode = orgCode;
            }

            public String getAlarmId() {
                return alarmId;
            }

            public void setAlarmId(String alarmId) {
                this.alarmId = alarmId;
            }

            public String getTimeDesc() {
                return timeDesc;
            }

            public void setTimeDesc(String timeDesc) {
                this.timeDesc = timeDesc;
            }

            public String getCameraName() {
                return cameraName;
            }

            public void setCameraName(String cameraName) {
                this.cameraName = cameraName;
            }

            public String getStationId() {
                return stationId;
            }

            public void setStationId(String stationId) {
                this.stationId = stationId;
            }

            public String getGbCode() {
                return gbCode;
            }

            public void setGbCode(String gbCode) {
                this.gbCode = gbCode;
            }

            public String getComments() {
                return comments;
            }

            public void setComments(String comments) {
                this.comments = comments;
            }

            public String getLibId() {
                return libId;
            }

            public void setLibId(String libId) {
                this.libId = libId;
            }

            public String getOrgName() {
                return orgName;
            }

            public void setOrgName(String orgName) {
                this.orgName = orgName;
            }

            /**
             * 某个布控库的子库名称
             *
             * @return
             */
            public String getCilNames() {
                if (TextUtils.isEmpty(cilNames) || cilNames.equals("null")) {
                    return "";
                }
                return "-" + cilNames;
            }

            public void setCilNames(String cilNames) {
                this.cilNames = cilNames;
            }

            public String getAlarmTime() {
                return alarmTime;
            }

            public void setAlarmTime(String alarmTime) {
                this.alarmTime = alarmTime;
            }

            public String getLibName() {
                if (TextUtils.isEmpty(libName) || libName.equals("null")) {
                    return "";
                }
                return libName;
            }

            public void setLibName(String libName) {
                this.libName = libName;
            }

            public String getSmallHttpUrl() {
                return smallHttpUrl;
            }

            public void setSmallHttpUrl(String smallHttpUrl) {
                this.smallHttpUrl = smallHttpUrl;
            }

            public String getDataStatus() {
                return dataStatus;
            }

            public void setDataStatus(String dataStatus) {
                this.dataStatus = dataStatus;
            }

            public Object getParentId() {
                return parentId;
            }

            public void setParentId(Object parentId) {
                this.parentId = parentId;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getCameraId() {
                return cameraId;
            }

            public void setCameraId(String cameraId) {
                this.cameraId = cameraId;
            }

            public String getBigHttpUrl() {
                return bigHttpUrl;
            }

            public void setBigHttpUrl(String bigHttpUrl) {
                this.bigHttpUrl = bigHttpUrl;
            }

            public String getIdcard() {
                return idcard;
            }

            public void setIdcard(String idcard) {
                this.idcard = idcard;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Object getCameraCode() {
                return cameraCode;
            }

            public void setCameraCode(Object cameraCode) {
                this.cameraCode = cameraCode;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public void setItemType(int itemType) {
                this.itemType = itemType;
            }

            @Override
            public int getItemType() {
                return itemType;
            }
        }
    }
}
