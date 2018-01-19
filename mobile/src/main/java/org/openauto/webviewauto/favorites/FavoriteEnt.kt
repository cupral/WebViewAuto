package org.openauto.webviewauto.favorites

class FavoriteEnt {

    var id: String? = null
    var title: String? = null
    var url: String? = null
    var favicon: String? = null
    var desktop: Boolean? = null

    constructor(id: String, title: String, url: String, favicon: String, desktop: Boolean?) {
        this.id = id
        this.title = title
        this.url = url
        this.favicon = favicon
        this.desktop = desktop
    }

    constructor(id: String, title: String, url: String, desktop: Boolean?) {
        this.id = id
        this.title = title
        this.url = url
        this.favicon = "nofavimg.png";
        this.desktop = desktop
    }


}
