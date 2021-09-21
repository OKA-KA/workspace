<template>
  <div id="app">
    <TableKoutsuhi :record="record" />
  </div>
</template>

<script>
import TableKoutsuhi from './TableKoutsuhi.vue';

export default {
  name: 'App',
  components: {
    TableKoutsuhi,
  },
  props: {
    record: {
      type: Object,
      default: undefined,
    },
  },
};
</script>
