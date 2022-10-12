class Proto {
    constructor(host) {
        this.host = host;
    }

    async call(path, object, defaultResponse=null) {
        try {
            let out = await (await fetch(this.host+"/"+path, {
                method: "POST", body: JSON.stringify(object)
            })).json();
            if (out == null) return defaultResponse;
            return out;
        } catch (e) {
            return defaultResponse;
        }
    }
}