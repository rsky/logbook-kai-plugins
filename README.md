logbook-kai-plugins
-------------------

sanaehirotakaさんの航海日誌 ([logbook-kai](https://github.com/sanaehirotaka/logbook-kai)) へのプラグイン集。

## pushbullet

航海日誌に[Pushbullet](https://www.pushbullet.com/)を使って遠征・入渠の完了をプッシュ通知する機能を追加します。  

### インストール方法

[Releases](https://github.com/rsky/logbook-kai-plugins/releases)より **pushbullet.jar** をダウンロードして航海日誌の **plugins** フォルダに入れ、航海日誌を再起動してください。


### 設定方法

1. 「その他」メニュー内の「Pushbullet」よりPushbulletの設定ウインドウを開きます。
2. Pushbulletの管理画面で取得したアクセストークンを入力し、「更新」ボタンを押します。
3. 登録されている端末およびチャンネルのリストが表示されるので、プッシュ通知したい対象をチェックして「OK」を押せば設定完了です。

![Pushbullet設定画面](./img/logbook-kai-pushbullet.png)

### 使用ライブラリとライセンス

以下のライブラリを使用しています。

#### [Retrofit](http://square.github.io/retrofit/)

* Apache License 2.0
* **ライセンス全文 :** [http://square.github.io/retrofit/#license](http://square.github.io/retrofit/#license)

#### [RxJavaFX](https://github.com/ReactiveX/RxJavaFX)

* Apache License 2.0
* **ライセンス全文 :** [https://github.com/ReactiveX/RxJavaFX/blob/0.x/LICENSE](https://github.com/ReactiveX/RxJavaFX/blob/0.x/LICENSE)

#### [Lombok](https://projectlombok.org/)

* MIT License
* **ライセンス全文 :** [https://github.com/rzwitserloot/lombok/blob/master/LICENSE](https://github.com/rzwitserloot/lombok/blob/master/LICENSE)

## ビルド方法

`lib` フォルダに航海日誌の `logbook-kai.jar` を入れて `mvn package`

## 宣伝

[航海日誌のmacOS用.appを作るツール](https://github.com/rsky/logbook-packager)あります。
