import { ComponentStory } from '@storybook/react';
import styled from 'styled-components';

import ProductDetail from '@/components/Product/ProductDetail/ProductDetail';

import { products } from '@/mocks/data';

export default {
  component: ProductDetail,
  title: 'Components/Product/ProductDetail',
};

const Container = styled.div`
  width: 500px;
`;

const Template: ComponentStory<typeof ProductDetail> = (args) => (
  <Container>
    <ProductDetail {...args} />
  </Container>
);

const defaultArgs = products[0];

export const Default = () => <Template product={defaultArgs} />;
