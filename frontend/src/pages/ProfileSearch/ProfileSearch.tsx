import { useState } from 'react';

import SearchBar from '@/components/common/SearchBar/SearchBar';
import SearchFilter from '@/components/SearchFilter/SearchFilter';

import * as S from '@/pages/ProfileSearch/ProfileSearch.style';
import useProfileSearch from '@/hooks/useProfileSearch';
import ProfileSearchResult from '@/components/ProfileSearchResult/ProfileSearchResult';
import Loading from '@/components/common/Loading/Loading';
import AsyncWrapper from '@/components/common/AsyncWrapper/AsyncWrapper';

function ProfileSearch() {
  const [careerLevelFilter, setCareerLevelFilter] = useState('');
  const [jobTypeFilter, setJobTypeFilter] = useState('');
  const [searchInput, setSearchInput] = useState('');

  const {
    profiles,
    getNextPage,
    isError: isProfileSearchError,
    isLoading: isProfileSearchLoading,
    isReady: isProfileSearchReady,
  } = useProfileSearch({
    query: searchInput,
    careerLevel: careerLevelFilter,
    jobType: jobTypeFilter,
    size: '4',
  });

  const handleCareerLevelFilterClick: React.MouseEventHandler<
    HTMLButtonElement
  > = (e) => {
    if (!(e.target instanceof HTMLButtonElement)) return;
    if (e.target.value === careerLevelFilter) {
      setCareerLevelFilter('');
      return;
    }
    setCareerLevelFilter(e.target.value);
  };

  const handleJobTypeFilterClick: React.MouseEventHandler<HTMLButtonElement> = (
    e
  ) => {
    if (!(e.target instanceof HTMLButtonElement)) return;
    if (e.target.value === jobTypeFilter) {
      setJobTypeFilter('');
      return;
    }
    setJobTypeFilter(e.target.value);
  };

  return (
    <S.Container>
      <S.SearchWrapper>
        <SearchBar searchInput={searchInput} setSearchInput={setSearchInput} />
        <SearchFilter
          careerLevelFilter={careerLevelFilter}
          jobTypeFilter={jobTypeFilter}
          handleCareerLevelFilterClick={handleCareerLevelFilterClick}
          handleJobTypeFilterClick={handleJobTypeFilterClick}
        />
      </S.SearchWrapper>
      <AsyncWrapper
        fallback={<Loading />}
        isReady={isProfileSearchReady}
        isError={isProfileSearchError}
      >
        <ProfileSearchResult
          data={profiles}
          getNextPage={getNextPage}
          isLoading={isProfileSearchLoading}
          isReady={isProfileSearchReady}
          isError={isProfileSearchError}
        />
      </AsyncWrapper>
    </S.Container>
  );
}

export default ProfileSearch;