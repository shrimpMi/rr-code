var vm = new Vue({
    el: '#cfgapp',
	data:{
        cfg:{}
	},
    created:function(){
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
        updateConfig: function() {
            console.log(vm.cfg)
            console.log(JSON.stringify(vm.cfg))
            var data = {'package':vm.cfg.package,'packages':vm.cfg.packages}
            $.post("sys/generator/config/save",JSON.stringify(data),function(res){
                console.log(res)
            });
            // $.ajax({
            //     url: 'sys/generator/config/save',
            //     type: 'post',
            //     data: {'json':JSON.stringify(vm.cfg)},
            //     success: function (res) {
            //         console.log(res)
            //     },
            //     error: function(xhr, errorType, error) {
            //         alert('Ajax request error, errorType: ' + errorType +  ', error: ' + error)
            //     }
            // });
		}
	}
});

