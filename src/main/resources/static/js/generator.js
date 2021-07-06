$(function () {
    $("#jqGrid").jqGrid({
        url: 'sys/generator/list',
        datatype: "json",
        colModel: [			
			{ label: '表名', name: 'tableName', width: 100, key: true },
			{ label: 'Engine', name: 'engine', width: 70},
			{ label: '表备注', name: 'tableComment', width: 100 },
			{ label: '创建时间', name: 'createTime', width: 100 }
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50,100,200],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });
});


var vm = new Vue({
	el:'#rrapp',
	data:{
		q:{
			tableName: null,
			dbName: null
		},
        db:[
            'wuse_order','wuse_goods'
        ],
        cfg:{}
	},
    created:function(){
        $.ajax({
            url: 'sys/generator/dblist',
            type: 'get',
            dataType: 'json',
            success: function (res) {
                console.log(res)
                vm.db = res.db;
            },
            error: function(xhr, errorType, error) {
                alert('Ajax request error, errorType: ' + errorType +  ', error: ' + error)
            }
        });
        $.ajax({
            url: 'sys/generator/config',
            type: 'get',
            dataType: 'json',
            success: function (res) {
                vm.cfg = res.data;
                console.log(res.data)
            },
            error: function(xhr, errorType, error) {
                alert('Ajax request error, errorType: ' + errorType +  ', error: ' + error)
            }
        });
    },
	methods: {
		query: function () {
			$("#jqGrid").jqGrid('setGridParam',{ 
                postData:{'tableName': vm.q.tableName,'dbName': vm.q.dbName},
                page:1 
            }).trigger("reloadGrid");
		},
		generator: function() {
            var tableNames = getSelectedRows();
            if(tableNames == null){
                return ;
            }
            location.href = "sys/generator/code?tables=" + tableNames.join() +"&dbName=" + vm.q.dbName;
		},
        updateConfig: function() {
            console.log(vm.cfg)
		}
	}
});

