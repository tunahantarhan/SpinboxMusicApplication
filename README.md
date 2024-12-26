# SpinBox Music Application

Çevrimiçi fiziksel müzik alışveriş platformu.

## 1.  ÖZET
    

SpinBox Music uygulaması, 2024 yılında okul projem doğrultusunda ortaya çıkmış bir fikir. Henüz lisans öğrenciliği aşamasında olan ve programlama/uygulama geliştirme adımlarını daha çok web tabanlı ve tasarımsal olarak uygulama geliştirme şeklinde atan bir öğrenci tarafından geliştirilmiş bir proje uygulama. Bu uygulamanın misyonu müzik severlere CD veya LP (plak) formatında ilgi duydukları albümlere ulaşım imkanı sağlar ve müzik dinleme alışkınlığındaki eski klasik tadı bu albümleri müzik severlere fiziksel formatta ulaştırmaktır. Kullanıcılara müzik ufuklarını genişletmek, sevdikleri favori sanatçılarına, gruplarına ve müziklerine ulaşmak, müziğin takibinde eski tadı yakalama imkanı sunar. Bu uygulamada kullanıcılar ürünleri inceleyebilir, ilgili bilgiler edinebilir, sepetine ekleyebilir, güvenli bir şekilde ödemesini yapabilir ve sipariş detaylarını görebilir. Uygulamada veri yönetimi için Firebase Realtime Database kullanılmış olunup, Android Studio programı üzerinden Kotlin yazılım dili ile geliştirilmiştir.

## 2.  GİRİŞ
    

SpinBox Music uygulaması, müzik albümlerine fiziksel formatta ulaşımın dijital ortamda kolayca sağlandığı bir çevrimici mağaza uygulamasıdır. SpinBox Music, kullanıcılar ve özellikle de müziğin takibinde eski klasik tadı, dokuyu arayanlar için geliştirilmiş bir uygulamadır. SpinBox Music müzikseverleri bir araya toplayan bir çevrimici platformdur. Günümüzde neredeyse her şey gibi müziğe ulaşmak da fazlasıyla büyük bir oranda dijitalleşmiştir. Bu gelişen ve büyüyen dijital ortamda müzik severlerin fiziksel olarak klasik dokuyu yakalama isteklerine karşılık vermek için böyle bir platformun gerekliliği ortaya çıkmıştır. Standart kullanıcılığın yanında koleksiyonerler için de oldukça büyük bir hizmet sunan SpinBox Music, büyük sayıda özel koleksiyonluk ürün versiyonları ile  geniş bir alışveriş yelpazesi sunmaktadır. SpinBox Music, kullanıcı dostu ve yüksek erişilebilirlikteki arayüzü, basit ve güvenilir ödeme sistemiyle müzik alışverişinin keyfini ve erişilebilirliğini arttırmaktadır. 

### A.  _Misyon_
    

SpinBox Music projesi başlangıcında belirtildiği üzere müzik severlere, müzik takibinde eski klasik dokuyu arayan müzik tutkunlarına yönelik hizmet sağlama amacıyla doğmuş bir projedir. SpinBox Music projesinin misyonu hedef kitlenin ve kullanıcıların CD veya LP (plak) formatında müzik albümlerine ulaşmasını, güvenli bir şekilde çevrimici olarak arzu ettikleri ürünlerin satın alımını ve alışverişini gerçekleştirerek mutlu bir kullanım deneyimi yaşamalarıdır. Uygulama üzerinden ürünlerin detaylı bir şekilde incelenmesi, hızlıca sepete eklenmesi ve satın alım işleminin güvenli bir şekilde tamamlanması misyon aranmış ve karşılanmıştır. Müzik konusunda her yaştan ve zevkten,  çok geniş bir kitleye ulaşmak amaçlanmıştır.

## 3.  YÖNTEM
    

SpinBox Music uygulaması, müzik albümlerini CD ve LP olarak dijital ortamda fiziksel formatta satışa sunan bir mobil uygulamak olma özelliğiyle Android platformu için geliştirilmiştir. Kullanıcı dostu, göze hitap eden, özenlice tasarlanmış arayüzü ve hızlı işlem kapasitesi gibi özellikleriyle; müzikseverlerin, müziğe ulaşmadaki eski dokuyu arayan kullanıcıların çevrimici bir şekilde kolayca fiziksel müziğe ulaşmasını sağlamaktadır.

Bu bölümde projenin geliştirilmesi sırasında kullanılan teknolojileri, programları, araçları, işleyiş modelleri ve iş akışları hakkında yazılı ve görsel bilgiler sunulacaktır.

### A.  _Geliştirme Ortamı ve Kullanılan Araçlar_
    

SpinBox Music uygulaması, Android işletim sistemi için geliştirilmiş bir uygulamadır ve Android için yazılım geliştirmede en yaygın araçlardan _\- belki de en yaygını - biri olan_ Android Studio IDE’si kullanılarak programlanmıştır. Uygulamanın geliştirildiği ana ortam olan Android Studio’da ilgili XML layout tasarımları ve Kotlin ile yazılmış iş akışı yazılımı ile uygulama her açıdan optimal bir performans vermektedir. Bu programlama sırasında yine Android programlama konusunda en popüler ve en yeni yaklaşım olan, Google tarafından “Android için resmi programlama dili”\[1\] olarak açıklanan Kotlin yazılım dili tercih edilmiştir. 2017’de ortaya çıkarılan Kotlin Android platformunda uygulama geliştirmenin en modern, güçlü, güvenilir ve önerilen yaklaşımıdır. Kotlin, Java ile uyumlu çalışarak Android SDK ile sıkıntısız bir şekilde entegre olur. Güvenli ve okunabiliritesi yüksek bir dil olan Kotlin, hata yönetimi tarafında da geniş kolaylıklar sağlayarak hız oranı yüksek, hata oranı düşük yazılımların hayata geçmesini sağlar. Proje için en önemli kısımlardan birisi de verilerin yönetimi ve bulutta saklanmasıdır. SpinBox Music projesinde en sağlam ve yenilikçi yaklaşımlardan olan Firebase’in Realtime Database ve Authentication teknolojileri kullanılarak verinin optimal bir şekilde yönetimi sağlanmıştır. Realtime Database tarafında verilen anlık bire-bir bir şekilde bulutta saklanması, senkronizasyonu ve erişimi sağlanmıştır. Böylece kullanıcı-uygulama arasındaki deneyimin keyfi arttırılmıştır. Firebase Authentication tarafında ise kullanıcıların sisteme kayıt olması, kendi hesaplarıyla giriş yapması ve uygulama içindeki işlemlerinin ulaşılabilirliği yüksek, güvenli olması sağlanmıştır.

### B.  _Kullanıcı Arayüzü Tasarımı (UI/UX)_
    

SpinBox Music uygulamasında kullanıcı arayüzü  (UI) tasarımı tarafında ana olarak kullanıcı dostu olması, göze hitap ederliği, estetik düzen, ve kullanıcı deneyimi (UX) göz önünde bulundurularak hayata geçmiş ve geliştirilmiştir. Müzik severler uygulama içerisinden diledikleri müzik albümünü rahatça inceleyebilir, her albümün dinamik bir şekilde hazırlanmış kendi özel ürün detayında albümlerin detaylarına göz gezdirebilir, kendine özel sepetine ürünlerini ekleyebilir, güvenli bir şekilde ödemesini yapabilir ve sipariş işlemini tamamlayarak “Siparişler” kısmında siparişlerini görebilir. Tüm bu işlemlerde kolay ulaşılabilirlik ve estetik göz önünde bulundurulan ana etmenlerdir. Tasarımdaki bahsedilen unsurlar:

*   Karşılama Ekranı (Welcome Screen Activity): Bu ekran uygulama ilk açıldığında kullanıcıları karşılayan, günün o anki vaktine göre (“Calendar”  importu ile _getInstance().get(Calendar, HOUR\_OF\_DAY_) komutu sayesinde günün vaktini alır) ilgili karşılama mesajını ekrana sunan ve kullanıcıyı giriş yapma veya kayıt olma ekranlarına yönlendiren ekrandır.
    
*   Giriş ve Kayıt Ekranları (SignInActivity & SignUpActivity): Kullanıcıların eğer daha önce kayıtlı olmuşsa giriş yapmalarını veya daha önce kayıt olmamışlarsa kayıt olmalarını sağlayan ekranlardır. Birbirleri ile bağlantılıdır ve ekranların içlerinde kullanıcı zaten kayıtlıysa giriş yapma ekranına, ya da kayıtlı değilse kayıt ol ekranına yönlendiren seçenekler vardır. InputLayout’lara girilen değerleri alarak Authentication tarafında read/write yapan ekranlardır.
    
*   Ana Ekran (Main Activity): Uygulamanın ana ekranıdır. Eğer daha önce giriş yapılmışsa ve çıkış yapma işlemi gerçekleştirilmemişse uygulama açıldığında SplashScreen’den sonra (uygulamaların açılışlardaki genellikle logolarının vb. olduğu ekran) direkt olarak bu ekran açılır. Bu ekranda her ekranın üst kısmında olduğu gibi _\- bir sonraki ekranlarda bu nedenle değinilmeyecektir -_ yandan açılır “drawer” menüsü, uygulama logosu, sepet ve profil ikonları vardır ve ilgili fonksiyonları gerçekleştirirler. Ekranın üstünde 2 banner görseli arasında dönen, belirlenmiş saniyede bir görseli diğeriyle değiştiren bir ViewFlipper bizi karşılamaktadır. Ekranın orta kısmında ise “Albümler” başlığı altında 3 adet albümün bulunduğu görselin altında tüm albümlerin görülmesi adına AlbumsActivity’ye yönlendiren bir buton vardır. Ekranın en altında ise yine tıklanınca OrdersActivity’ye yönlendiren “Siparişlerim” yazılı bir banner vardır.
    
*   Albümler Ekranı (AlbumsActivity): Veritabanından “products” altındaki bütün albüm ürünlerini bir RecyclerView içerisinde album\_item şeklinde kartlar olarak listeleyen, gerekli ana bilgilerini (isim, sanatçı, fiyat) gösteren ve her ürünün yanında bulunan ikon ile ilgili ürünün ürün detay sayfasına (ShopDetailActivity) gitmeyi sağlayan ekrandır. Albümlere topluca ana ulaşım bu ekrandan gerçekleştirilir.
    
*   Ürün Detay Sayfası (ShopDetailActivity): Seçilen ürünün ilgili verilerini XML layout’unda belirlenen id’li yerlere Firebase Realtime Database üzerinden çekerek yerleştiren sayfadır. Ürünlerin daha detaylı özellikleri (genre, sanatçı, puan, yıl, ülkesi, stok durumu vb.) gibi özellikleri bu sayfada listelenir. RadioButton özelliğiyle ürün CD veya LP olarak seçilir ve seçilen formatın fiyatına göre stok sayısı 0’dan yüksek olduğu sürece “Sepete Ekle” butonu kullanılarak sepete eklenir. Eklenen ürünler veri tabanında “cart” klasörü altında her kullanıcının kendi uid’si ile oluşan klasörün içinde product’ın kendisi olarak listelenir.
    
*   Sepet Sayfası (CartActivity) : Her kullanıcının kendi uid’si ile database üzerinden “cart” klasöründen alınan sepet verileri her kullanıcının olması gerektiği gibi kendisine özgüdür. CartAdapter kullanarak cart\_item şeklinde oluşturulan sepete eklenmiş ürünler sepette listelenir. Kullanıcılar isterse sepetteki ürünlerin adetini arttırabilir, azaltabilir ve adet sayısı 0 olunca sepetten çıkartmış olabilir. Sepette ürünlerin adı, sanatçısı, adeti vb. gösterilir. Eklenen her ürünün fiyatı toplam ücret olarak Sipariş Ver butonunun yanında gösterilir.  
    
*   Sipariş Oluşturma Ekranı (CheckoutActivity): Bu ekranda kullanıcıyı adres, kredi kartı numarası, kredi kartı son kullanım ayı, yılı ve CVV numarasının girilmesini isteyen InputLayout’lar karşılar. Kullanıcı tüm kutucukları uygun doldurursa Siparişi Tamamla butonuna tıklar. Böylece siparişin tamamlandığını belirten Toast mesajı ekrana verilir ve kullanıcının sepeti temizlenerek ana ekrana geri yönlendirilir. Ayrıca siparişi verilen ürünlerin stok değerleri verildiği adet kadar düşürülür.
    
*   Siparişler Ekranı (OrdersActivity): Yine sepet ekranına benzer bir şekilde burada da kullanıcılar verdikleri siparişleri bir RecyclerView içerisinde order\_item şeklindeki kartlarla listelenmiş olarak görür. Burada ek bir özellik olarak siparişin verilidiği dd/MM/yy hh:mm değeri her kartın içerisinde gözükür.
    

### C.  _Admin Arayüzü Tasarımı_
    

SpinBox Music uygulamasına admin rolüne sahip bir kullanıcı olarak giriş yapıldığında profil ekranında ekstra bir “Admin Arayüzü” butonu çıkar ve böylece sadece admin rolündeki kullanıcılar bu arayüze ve devamındaki sayfalara erişim sağlar.

Admin arayüzünün ana ekranında üç adet banner şeklinde yönlendirme vardır. “Kullanıcı Yönetimi” banner’ına tıklama ile admin olan kullanıcı AdUserManagementActivity sayfasına giderek her zamanki gibi RecyclerView içerisinde kartlarla listelenmiş tüm kullanıcıların bilgilerini görebilir, üyelik durumlarını “aktif” veya “dondurulmuş” olarak ayarlayabilir ve hatta kullanıcıyı silebilir.

“Ürün Yönetimi” banner’ına tıklanması durumunda AdAlbumsManagementActivity’ye gidilir ve burada yine AlbumsActivity’deki gibi AlbumAdapter kullanılarak listelenen bütün albümler, aynı şekilde burada ek özelliklerle listelenir. Her ürünün kartında “Düzenle” ve “Sil” şeklinde iki buton bulunur. Bu butonlardan “Sil” olan seçilirse ilgili ürün products klasöründen silinir, ve dolayısıyla anlık olarak uygulamadan ve sistemden de kaldırılmış olur. Düzenle butonu ile ise ayrı bir düzenleme ekranına gidilir ve EditText’ler olarak alınan CD fiyatı, LP fiyatı, stok sayısı gibi özellikler buradan anlık bir şekilde güncellenir ve database’e iletilir.

Son olarak “Sipariş Yönetimi” sayfası ile kullanıcıların eriştiği databasedeki “orders” kısmının ilgili kendi uid’leri altındaki siparişlerden ziyade aynı sistemi kullanarak admin direkt olarak “orders” klasöründeki bütün uid’lerin, dolayısıyla  tüm kullanıcıların siparişlerini görüntüleyebilir. Ekranın en altında bulunun sipariş onay butonu ile ise bu siparişleri onaylar ve kendi tarafında işlemini gerçekleştirdiği için siparişler ekranını boşaltır

## 4.  _Deneysel Sonuçlar, Uygulama Mantığı ve İş Akışları_
    

Bu kısımda uygulamadaki bazı ana fonksiyonların iş akışlarını detaylıca inceleyeceğiz. Uygulamanın tüm geliştirilme aşaması şahsi olarak benim için bu konuda bilgisi olmayan bir lisans öğrencisi için deneysel ve öğretici olmakla beraber, bu konuda tecrübe kazanmak benim için en iyi yanlardandı. Yöntem kısmında sayfalarda kullanılan  uygulama mantıklarından ve akışından genellikle bahsettiğim için burada daha rahat bir şekilde ifade edeceğim. Başlıca ana fonksiyonlar şöyledir: 

*   Albümlerin Listelenmesi: Realtime Database üzerinden products klasörü içerisinde her albümün product.title değeri ile oluşturulmuş klasörler şeklinde saklanan albümlerin, bu klasörlerin de içinde cdPrice, lpPrice, country, title, artist, year, stock vb. verileri bulunmaktadır. RecyclerView ile AlbumAdapter kullanılarak her bir albüm title, artist, cdPrice ve lpPrice bilgileriye listelenmektedir. ShopDetail sayfası ise aynı mantıkta fakat RecycleView gibi listelemek yerine ürün detayı görmek istenilen ürünün products klasörü altındaki product.title kullanılarak bilgileri alınması ve ürün detay sayfasında dinamik olarak bilgilerin girilmesi şeklinde gerçekleşir.
    
*   Sepet Sistemi: Sepete ekleme, albüm detay ekranında sepete ekle butonuna basılmasıyla database üzerinden cart klasöründe her kullanıcının kendi uid klasörü altında sepete eklemek için butona tıkladığı ürünün products altındaki gibi listelenmesiyle gerçekleşir.
    
*   Sipariş Tamamlama Sistemi: Sepet sayfasında sepet boş olmadığı sürece eklenen ürünlerin toplam fiyatını ödeme tamamlama ekranına aktararak gösteren bir ödemeye geçme butonu vardır. Bu buton ile ödeme sayfası açılır ve ilgili input kutularına ilgili bilgiler kutu tarzına uygun girilmişse siparişi tamamlama butonu çalışır. Siparişin alındığı mesajını Toast olarak verir, sepeti temizler. Siparişi veri tabanında orders klasörü altında uid’ler içerisinde ürünlerin siparişinin verildiği anın milisaniye cinsinden değeri ile isimlendirilmiş belgelerde saklanır.
    

## 5.  Sonuç
    

SpinBox Music uygulaması, müzik severlerin klasik bir şekilde fiziksel olarak albüm satın alma isteklerinin dijital ortamda karşılandığı bir platform projesidir. Bu uygulama klasik ve eski dokuyu içinde bulunduran fiziksel albümlerin satışını kolaylaştırmayı amaçlayan başarılı bir projedir. Kullanıcı dostu, hızlı, optimal ve güvenli arayüzü, senkronize gelişmiş veritabanı bağlantıları, veri işleme yapısı ile sofistike ve modern bir uygulamadır. Firabase ve Kotlin yazılım dili gibi alanında en gelişmiş ve modern teknolojik imkanlar sayesinde diğer uygulamaların önüne geçmeyi başarmıştır. Kotlin diliyle yazılan uygulama doğal olarak modern yenilikçi, okunulabilir, rahat ve güvenli bir yapı sunar. Alanında en önde gelen Android uygulama geliştirme ortamı olan Android Studio ile de en üst düzeyde verimli bir geliştirme ortamı sağlanmıştır. 

Uygulama, sunduğu hesap oluşturma/girişi, yönlendirme seçenekleri, dinamik albüm listelelemesi, dinamik bir şekilde bilgilerle donatılan ürün detay sayfası, kişiye özel sepet ve siparişler sistemi, güvenli ödeme tamamla yapısı ile kullanıcılara sorunsuz ve kaliteyi en üst seviyede hissettiren bir alışveriş deneyimin kapılarını açmaktadır.

Sonuç olarak SpinBox Music uygulaması; müzik alışverişini eski klasik yol ile; ama en iyi günümüz imkanlarıyla yapmak isteyen kullanıcılar için kolay kullanımlı, hız ve güvenlik kriterleri had safhada bir dijital alışveriş platformu sunmaktadır. Sadece müzik severler değil, aynı zamanda yapımcılar için de yeni bir iş pazarı sağlamaktadır. Ürünlerini uygulama üzerinden isterlerse satışa sunmak için başvuruda bulunabilecek olan sanatçılar, gruplar, yapımcılar ve müzik şirketleri için de SpinBox Music bir vizyon kapısını aralamaktadır. Yeni pazar imkanları sunmanın yanı sıra, proje aynı zamanda eski klasik müzik anlayışının günümüz dünyasında yaşaması için de hayati bir konumdadır. Yapımcıların tamamen dijitale yönelmeye hızla devam ettiği bu devirde, fiziksel müzik takibinin hayatına devam etmesi için de büyük bir teşvik ve imkan sağlamaktadır.

İlerleyen süreçte yolumuza hız kesmeden başarıyla devam etmek istemekle birlikte, daha fazla özelliklerin eklenmesiyle, her geçen gün kullanıcı memnuniyet seviyesinin arttılmasıyla üstüne koyarak kararlı bir şekilde ilerleme hedefindeyiz. Bu uygulama, müzik endüstrisi ve pazarını dijital dünya ile bir araya getirmesi nedeniyle büyük bir projedir.

## F.  Kaynakça
    

- Digitalogy, “Top Programming Languages for Android App Development,”, section 3, March 2024.
    
- W3 Schools, Kotlin
    
- Android Developers, “Design & Plan”
    
- Adalo Help, “How to Create Multi-Sided Apps,”, February 2024.
    
- GitHub Issues, “How to update a user’s email no that updateEmail(to: email) is deprecated,”, December 2023
