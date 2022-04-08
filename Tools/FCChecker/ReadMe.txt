--------------------------------------------------------------------------------
[[[ FileCode Checker ver 1.2.0 ]]]                                    2014/10/02
--------------------------------------------------------------------------------
[作者]      開発室ルドルフ/Nyuki
[mail]      studio.rudolph@gmail.com
[web]       http://www.project-ia.org/
[種別]      フリーウェア
[動作環境]  Windows Vista/Windows 7
--------------------------------------------------------------------------------

1. 概要 ------------------------------------------------------------------------
  FileCode Checkerはファイルの文字コードと改行コードをチェックするソフトウェアです。
  複数のファイルを一括してチェックすることができます。
  また、チェックしたファイルの文字コードまたは改行コードを指定した形式に変換することができます。

  文字コードの推定、変換部分はnkf32.dllを使用しています。
  nkf32に関しては以下のサイトを参照下さい。
    http://sourceforge.jp/projects/nkf/


2. インストール ----------------------------------------------------------------
  アーカイブを展開して出来たフォルダを任意の場所へコピーしてください。


3. アンストール ----------------------------------------------------------------
  上記のフォルダ/ファイルを削除して下さい。


4. 使用方法 --------------------------------------------------------------------
  (1) FCChecker.exeを実行します。

  (2) ダイアログのリストビューにファイル、またはフォルダをドロップします。

  (3) 文字コード、改行コードのチェック結果がリストビューに表示されます。

  (4) 必要であれば、文字コードもしくは改行コードを選択し、
     「変換」ボタンを押し、コードの変換を実施します。

5. 更新履歴 -------------------------------------------------------------------
  2014/10/02 1.2.0版ビルド
    ・改行コードが混在していた場合、結果欄に「混在」と表示するように修正。
    ・除外対象だけでなく、チェック対象とするファイルを指定できる機能を追加。
    ・チェック/除外対象とする条件を正規表現で指定できる機能を追加。
    ・隠しファイル属性のファイルを変換した際に、エラーとなっていた不具合を修正。
    ・変換時に別のファイルとして出力した場合、元ファイルの属性を引き継ぐよう修正。
    ・パスに空白等を含むファイルを外部アプリケーションで開いた場合に、
      正しく開けていなかった不具合を修正。

  2013/07/10 1.1.1版ビルド 
    ・開発環境更新時にXPサポートがオフになっていたので再ビルド

  2013/06/19 1.1.0版ビルド 
    ・チェック結果をテキストファイルに出力する機能を追加
    ・同梱のnkf32.dllを2.0.7から2.1.2へ変更

  2009/09/17 1.0.0版ビルド 
    ・公開開始

6. 使用上の注意 ----------------------------------------------------------------
  このプログラムはフリーウェアです。
  このプログラムを使用することで発生した
  いかなるトラブルに関して作者は責任を負いかねます。

  バグ報告や機能追加の要望、意見などありましたら
  下記のメールまでお願いします。
  studio.rudolph@gmail.com


7. 謝辞 ------------------------------------------------------------------------
  ソフトウェアの作成にあたりnkf32.dllを利用させていただきました。
  nkfのメンテナの方々、またプロジェクト関連各位の皆様に深く感謝いたします。

  nkf 1.4
    morb@fujitsu, kiss@ayumi.stars.flab.fujitsu, cen122@flab.fujitsu,
    yuki@flab.fujitsu 他、fujitsu & flab.fujitsu の皆さんの協力に感謝。
    shinoda@cs.titech, kato@cs.titech, uematsu@cs.titech TNX
    kono@ie.u-ryukyu.ac.jp

  nkf 1.9-
    河野真治
    Akio Furukawa
    Andy Taki
    From: OHARA Shigeki
    Fumitaka Kitagawa
    Hiroaki Sengoku
    Ikuhiro MORITA (森田 育宏)
    Yoshiharu ITO
    Junn Ohta
    KAWAMURA Masao
    Kazuhiko Mori
    Keitaro Isokawa
    Ken-ichi Hirose
    Ki-ichiro SATO
    Kiwamu Aoyama
    Koichi Hirayama
    Mitsuru Hase (長谷　満)
    OHARA Shigeki (大原 重樹)
    Rei FURUKAWA
    Satoru Takabayashi
    Shigeyuki Takagi
    Shin MICHIMUKO
    Tadamasa Teranishi
    TOYODA Jiro
    TSUCHIYA Masatoshi
    Tsutomu Sakai
    YAMASHITA Junji (山下 純司)
    Yasuyuki Sato
    Yoshiaki Yanagihara
    hat@so-net
    ＤＣＣ技術部渡辺
    かとぺ / 加藤 貴司
    かべdais
    ひろせ まさあき
    イントラネットシステム(株)松尾
    鵜飼文敏
    塩崎 毅彦(SHIOZAKI Takehiko)
    河村雅夫 (Media Lab.)
    河野 康司
    喜瀬“冬猫”浩＠南国沖縄
    金井 智彦
    桂田 祐史 (かつらだ まさし)
    高橋宜盟
    国吉
    黒江明彦
    三宅正泰
    山倉 真
    山本 芳人
    山野裕司 (やまのゆうじ)
    重村法克
    小島基靖
    上田　健
    新井 康司 (Koji Arai)
    森　千絵子
    森　和彦
    水野 貴文
    前地和俊
    仲宗根＠並列信頼研
    猪谷 英一郎 == Eiichiro Itani
    藤原　秀行
    八田 真行 (Masayuki Hatta)
    尾川敏也
    望月 孝志
    木村 (＠筑波大学数学研究科)
    野村俊彰
    有岡＠ミノルタ
    有沢 明宏
    落合一喜
    林 亮
    國安 治
    木下 雄介
    田中隆裕
    鶴谷直樹
    中田伸悦
    わたなべひろふみ
    MoonWolf
    sava

AUTHOR
    市川 至 ichikawa@flab.fujitsu.co.jp (was ichikawa@fujitsu.JUNET)
    河野 真治 kono@ie.u-ryukyu.acjp
    Rei FURUKAWA furukawa@tcp-ip.or.jp
    成瀬
    mastodon

    http://sourceforge.jp/projects/nkf/

    Copyright (C) 1987, FUJITSU LTD. (I.Ichikawa),2000 S. Kono, COW
    Copyright (C) 2002-2006 Kono, Furukawa, Naruse, mastodon

