package com.woowacourse.f12.domain.product;

import static com.woowacourse.f12.domain.member.CareerLevel.JUNIOR;
import static com.woowacourse.f12.domain.member.CareerLevel.SENIOR;
import static com.woowacourse.f12.domain.member.JobType.BACKEND;
import static com.woowacourse.f12.domain.member.JobType.MOBILE;
import static com.woowacourse.f12.domain.product.Category.KEYBOARD;
import static com.woowacourse.f12.support.MemberFixtures.CORINNE;
import static com.woowacourse.f12.support.MemberFixtures.MINCHO;
import static com.woowacourse.f12.support.ProductFixture.KEYBOARD_1;
import static com.woowacourse.f12.support.ProductFixture.KEYBOARD_2;
import static com.woowacourse.f12.support.ReviewFixtures.REVIEW_RATING_1;
import static com.woowacourse.f12.support.ReviewFixtures.REVIEW_RATING_2;
import static com.woowacourse.f12.support.ReviewFixtures.REVIEW_RATING_4;
import static com.woowacourse.f12.support.ReviewFixtures.REVIEW_RATING_5;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.woowacourse.f12.config.JpaConfig;
import com.woowacourse.f12.domain.member.Member;
import com.woowacourse.f12.domain.member.MemberRepository;
import com.woowacourse.f12.domain.review.Review;
import com.woowacourse.f12.domain.review.ReviewRepository;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

@DataJpaTest
@Import(JpaConfig.class)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void 제품을_단일_조회_한다() {
        // given
        Product product = 제품_저장(KEYBOARD_1.생성());
        Member member = memberRepository.save(CORINNE.생성());
        리뷰_저장(REVIEW_RATING_4.작성(product, member));
        리뷰_저장(REVIEW_RATING_5.작성(product, member));
        entityManager.flush();
        entityManager.refresh(product);

        // when
        Product savedProduct = productRepository.findById(product.getId())
                .orElseThrow(IllegalArgumentException::new);

        // then
        assertAll(
                () -> assertThat(savedProduct.getRating()).isEqualTo(4.5),
                () -> assertThat(savedProduct.getReviewCount()).isEqualTo(2)
        );
    }

    @Test
    void 키보드_전체_목록을_페이징하여_조회한다() {
        // given
        Product product1 = 제품_저장(KEYBOARD_1.생성());
        제품_저장(KEYBOARD_2.생성());
        Pageable pageable = PageRequest.of(0, 1);

        // when
        Slice<Product> slice = productRepository.findPageByCategory(KEYBOARD, pageable);

        // then
        assertAll(
                () -> assertThat(slice.hasNext()).isTrue(),
                () -> assertThat(slice.getContent()).containsExactly(product1)
        );
    }

    @Test
    void 키보드_전체_목록을_리뷰_많은_순으로_페이징하여_조회한다() {
        // given
        Product product1 = 제품_저장(KEYBOARD_1.생성());
        Product product2 = 제품_저장(KEYBOARD_2.생성());
        Member member = memberRepository.save(CORINNE.생성());

        리뷰_저장(REVIEW_RATING_5.작성(product1, member));
        리뷰_저장(REVIEW_RATING_5.작성(product2, member));
        리뷰_저장(REVIEW_RATING_5.작성(product2, member));

        Pageable pageable = PageRequest.of(0, 1, Sort.by(Order.desc("reviewCount")));

        // when
        Slice<Product> slice = productRepository.findPageByCategory(KEYBOARD, pageable);

        // then
        assertAll(
                () -> assertThat(slice.hasNext()).isTrue(),
                () -> assertThat(slice.getContent()).containsExactly(product2)
        );
    }

    @Test
    void 키보드_전체_목록을_평균_평점_순으로_페이징하여_조회한다() {
        // given
        Product product2 = 제품_저장(KEYBOARD_1.생성());
        Product product1 = 제품_저장(KEYBOARD_2.생성());
        Member member = memberRepository.save(CORINNE.생성());

        리뷰_저장(REVIEW_RATING_2.작성(product1, member));
        리뷰_저장(REVIEW_RATING_1.작성(product1, member));
        리뷰_저장(REVIEW_RATING_5.작성(product2, member));
        리뷰_저장(REVIEW_RATING_4.작성(product2, member));

        Pageable pageable = PageRequest.of(0, 1, Sort.by(Order.desc("rating")));

        // when
        Slice<Product> slice = productRepository.findPageByCategory(KEYBOARD, pageable);

        // then
        assertAll(
                () -> assertThat(slice.hasNext()).isTrue(),
                () -> assertThat(slice.getContent()).containsExactly(product2)
        );
    }

    @Test
    void 제품에_대한_사용자_연차의_총_개수를_반환한다() {
        // given
        Product product = 제품_저장(KEYBOARD_1.생성());

        Member corinne = CORINNE.생성();
        corinne.updateCareerLevel(SENIOR);
        corinne.updateJobType(MOBILE);
        corinne = memberRepository.save(corinne);

        Member mincho = MINCHO.생성();
        mincho.updateCareerLevel(JUNIOR);
        mincho.updateJobType(BACKEND);
        mincho = memberRepository.save(mincho);

        리뷰_저장(REVIEW_RATING_2.작성(product, corinne));
        리뷰_저장(REVIEW_RATING_1.작성(product, mincho));

        // when
        List<CareerLevelCount> careerLevelCounts = productRepository.findCareerLevelCountByProductId(
                product.getId());

        // then
        assertThat(careerLevelCounts).usingRecursiveFieldByFieldElementComparator()
                .hasSize(2)
                .containsOnly(
                        new CareerLevelCount(JUNIOR, 1),
                        new CareerLevelCount(SENIOR, 1)
                );
    }

    @Test
    void 제품에_대한_사용자_직군의_총_개수를_반환한다() {
        // given
        Product product = 제품_저장(KEYBOARD_1.생성());

        Member corinne = CORINNE.생성();
        corinne.updateCareerLevel(SENIOR);
        corinne.updateJobType(MOBILE);
        corinne = memberRepository.save(corinne);

        Member mincho = MINCHO.생성();
        mincho.updateCareerLevel(JUNIOR);
        mincho.updateJobType(BACKEND);
        mincho = memberRepository.save(mincho);

        리뷰_저장(REVIEW_RATING_2.작성(product, corinne));
        리뷰_저장(REVIEW_RATING_1.작성(product, mincho));

        // when
        List<JobTypeCount> jobTypeCounts = productRepository.findJobTypeCountByProductId(product.getId());

        // then
        assertThat(jobTypeCounts).usingRecursiveFieldByFieldElementComparator()
                .hasSize(2)
                .containsOnly(
                        new JobTypeCount(MOBILE, 1),
                        new JobTypeCount(BACKEND, 1)
                );
    }

    private Product 제품_저장(Product product) {
        return productRepository.save(product);
    }

    private Review 리뷰_저장(Review review) {
        return reviewRepository.save(review);
    }
}