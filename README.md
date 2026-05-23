# Web Tabanlı Kargo İşletme ve Rota Optimizasyonu Sistemi 📦🚚

Bu proje, **Kocaeli Üniversitesi Bilgisayar Mühendisliği** Yazılım Laboratuvarı-I kapsamında geliştirilmiş; Kocaeli ilçe merkezlerinden Kocaeli Üniversitesi'ne yapılacak kargo taşımacılığı için en uygun araç ve rota planlamasını gerçekleştiren full-stack bir kargo yönetim uygulamasıdır.

## 🚀 Projenin Amacı
Kargo miktarları, ağırlıkları, istasyon konumları, araç kapasiteleri ve kiralama/yakıt maliyetlerini analiz ederek toplam taşıma maliyetini minimize eden optimum rota çözümleri üretmektir. Sistem, hem sınırlı hem de sınırsız araç senaryolarına göre akıllı dağıtım planları oluşturur.

## 🛠️ Mimari ve Teknolojik Altyapı
* **Backend (Arka Uç):** Java / Spring Boot & RESTful API mimarisi
* **Frontend (Ön Uç):** JavaScript / React & Harita entegrasyonu
* **Veritabanı (RDBMS):** MySQL (İlişkisel veritabanı tasarımı ve E/R şemaları)
* **Optimizasyon:** Kapasite-maliyet odaklı dinamik rota planlama algoritmaları

## 🕹️ Sistem Özellikleri ve Senaryolar
1. **Dinamik Optimizasyon Algoritması:** Tanımlanan araçların (Küçük, Orta, Büyük ve Kiralık Araçlar) kapasite ve günlük maliyet parametrelerine göre yük ataması yapılır.
2. **Çoklu Senaryo Desteği:** 
   * *Sınırlı Araç Durumu:* Eldeki mevcut araç filosuyla maksimum verimlilikte dağıtım planlanır.
   * *Sınırsız Araç Durumu:* Talebi karşılamak adına maliyet-etkin kiralık araç takviyesiyle rotalar oluşturulur.
3. **Yönetici Panel Fonksiyonları:** İstasyon (durak) ekleme, araç filosu yönetimi ve harita üzerinde tüm sefer rotalarının ve toplam maliyet analizlerinin anlık takibi.
4. **Kullanıcı Panel Fonksiyonları:** Kargo gönderim talebi oluşturma ve kargoların taşındığı araca ait güzergâh bilgilerini harita üzerinden sorgulama.

## 📊 Veritabanı İlişki Modeli (E/R)
Sistem; `USER`, `CARGO`, `VEHICLE` ve `STATION` tabloları arasındaki ilişkisel bütünlüğü MySQL altyapısıyla korur.
* Her kargo bir kullanıcıya aittir ve belirli bir ağırlık/hacim değerine sahiptir.
* Rota optimizasyonu sonucunda kargolar uygun kapasitedeki araçlara dinamik olarak atanır.

## 👥 Geliştiriciler
* **Merve Kübra ÖZTÜRK**
* **İclal ÜSTÜN**
