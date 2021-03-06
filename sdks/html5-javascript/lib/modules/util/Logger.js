//Logger
(function() {
    var name = 'Logger', global = this, overwrittenName = global[name], exports;
    /* logging */
    function Logger(name) {
        this.logEnabled = true;
        this.init(name, true);
    }
    Logger.METHODS=[
        "log", "error", "warn", "info", "debug", "assert", "clear", "count",
        "dir", "dirxml", "exception", "group", "groupCollapsed", "groupEnd",
        "profile", "profileEnd", "table", "time", "timeEnd", "trace"
    ];
    Logger.prototype.init=function(name, logEnabled){
        this.name=name||"UNKNOWN";
        this.logEnabled=logEnabled||true;
        var addMethod=function(method){this[method]=this.createLogMethod(method)}.bind(this);
        Logger.METHODS.forEach(addMethod)
    }
    Logger.prototype.createLogMethod=function(method){
        return Logger.prototype.log.bind(this, method);
    }
    Logger.prototype.prefix=function(method, args){
        var prepend='['+method.toUpperCase()+']['+name+"]:\t";
        if(['log', 'error', 'warn', 'info'].indexOf(method)!==-1){
            if("string"===typeof args[0]){
                args[0]=prepend+args[0];
            }else{
                args.unshift(prepend);
            }
        }
        return args;
    }
    Logger.prototype.log=function(){
        var args=[].slice.call(arguments);
        method=args.shift();
        if(Logger.METHODS.indexOf(method)===-1){
            method="log";
        }
        if(!(this.logEnabled && console && console[method]))return;
        args=this.prefix(method, args);
        console[method].apply(console, args);
    }
    Logger.prototype.setLogEnabled=function(logEnabled){
        this.logEnabled=logEnabled||true;
    }

    Logger.mixin	= function(destObject){
        destObject.__logger=new Logger(destObject.name||"UNKNOWN");
        var addMethod=function(method){
            if(method in destObject.prototype){
                console.warn("overwriting '"+method+"' on '"+destObject.name+"'.");
                console.warn("the previous version can be found at '_"+method+"' on '"+destObject.name+"'.");
                destObject.prototype['_'+method]=destObject.prototype[method];
            }
            destObject.prototype[method]=destObject.__logger.createLogMethod(method);
        };
        Logger.METHODS.forEach(addMethod)
    }
    global[name] =  Logger;
    global[name].noConflict = function() {
        if(overwrittenName){
            global[name] = overwrittenName;
        }
        return Logger;
    };
    return global[name];
}());
